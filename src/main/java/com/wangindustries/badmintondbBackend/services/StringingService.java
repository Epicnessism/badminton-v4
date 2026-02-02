package com.wangindustries.badmintondbBackend.services;

import com.wangindustries.badmintondbBackend.exceptions.InvalidStateTransitionException;
import com.wangindustries.badmintondbBackend.models.Stringing;
import com.wangindustries.badmintondbBackend.models.StringingState;
import com.wangindustries.badmintondbBackend.repositories.StringingRepository;
import com.wangindustries.badmintondbBackend.requests.CreateStringingRequest;
import com.wangindustries.badmintondbBackend.requests.UpdateStringingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class StringingService {

    @Autowired
    private StringingRepository stringingRepository;

    /**
     * Creates a new Stringing entity.
     *
     * <p><b>Why Two DynamoDB Items Are Created:</b></p>
     * <p>
     * We use a single GSI (name-index) to support two access patterns:
     * <ul>
     *   <li>Query by stringer: gsiPk = "STRINGER#{stringerUserId}"</li>
     *   <li>Query by owner: gsiPk = "OWNER#{ownerUserId}"</li>
     * </ul>
     * Since a DynamoDB item can only have ONE value for gsiPk, we cannot use
     * a single item to appear in both GSI partitions. Therefore, we create:
     * <ol>
     *   <li><b>Main Item</b> (PK=STRINGING#{id}, SK=DETAILS): Contains all stringing data.
     *       Its gsiPk is set for stringer lookups.</li>
     *   <li><b>Owner Index Item</b> (PK=STRINGING#{id}, SK=OWNER#{ownerUserId}): A sparse item
     *       containing only the stringingId. Its gsiPk is set for owner lookups.</li>
     * </ol>
     * </p>
     *
     * <p><b>Update Implications:</b></p>
     * <p>
     * The owner index item is intentionally sparse (contains only stringingId) so that
     * updates only need to modify the main DETAILS item. When querying by owner, we get
     * the stringingId from the sparse item and the caller can fetch full details separately
     * if needed, OR we return the sparse result and let the client do a follow-up call.
     * </p>
     *
     * <p><b>Alternative Approaches:</b></p>
     * <ul>
     *   <li><b>Duplicate full data in both items:</b> Simpler queries but requires updating
     *       both items on every change (more writes, eventual consistency risk).</li>
     *   <li><b>Use two separate GSIs:</b> One for stringer, one for owner. Avoids dual items
     *       but requires additional GSI (user requirement was to use existing name-index only).</li>
     *   <li><b>Composite key in main table:</b> Store owner relationship in sort key patterns,
     *       but this complicates the primary access pattern (get by ID).</li>
     * </ul>
     */
    public Stringing createStringing(CreateStringingRequest request) {
        log.info("Creating stringing with request: {}", request);

        UUID stringingId = UUID.randomUUID();
        Instant now = Instant.now();

        // Main item: contains all stringing details, indexed by stringer in the GSI
        Stringing stringing = new Stringing();
        stringing.setPK(Stringing.createPk(stringingId));
        stringing.setSK(Stringing.createSkDetails());
        stringing.setStringingId(stringingId);
        stringing.setStringerUserId(request.getStringerUserId());
        stringing.setOwnerUserId(request.getOwnerUserId());
        stringing.setRacketMake(request.getRacketMake());
        stringing.setRacketModel(request.getRacketModel());
        stringing.setStringType(request.getStringType());
        stringing.setStringColor(request.getStringColor());
        stringing.setMainsTensionLbs(request.getMainsTensionLbs());
        stringing.setCrossesTensionLbs(request.getCrossesTensionLbs());
        stringing.setState(StringingState.REQUESTED_BUT_NOT_DELIVERED);
        stringing.setCreatedAt(now);
        stringing.setRequestedAt(now);

        if (request.getStringerUserId() != null) {
            stringing.setGsiPk(Stringing.createGsiStringerPk(request.getStringerUserId()));
            stringing.setGsiSk(Stringing.createGsiSk(stringingId));
        }

        stringingRepository.saveStringing(stringing);

        // Owner index item: sparse item for owner GSI lookups (only contains stringingId)
        // This allows querying "all stringings for owner X" without duplicating full data.
        // Updates only need to modify the main DETAILS item since this is just an index pointer.
        if (request.getOwnerUserId() != null) {
            Stringing ownerIndexItem = new Stringing();
            ownerIndexItem.setPK(Stringing.createPk(stringingId));
            ownerIndexItem.setSK(Stringing.createSkOwner(request.getOwnerUserId()));
            ownerIndexItem.setStringingId(stringingId);
            ownerIndexItem.setGsiPk(Stringing.createGsiOwnerPk(request.getOwnerUserId()));
            ownerIndexItem.setGsiSk(Stringing.createGsiSk(stringingId));

            stringingRepository.saveStringing(ownerIndexItem);
        }

        log.info("Successfully created stringing: {}", stringing);
        return stringing;
    }

    public Stringing getStringing(UUID stringingId) {
        log.info("Getting stringing with id: {}", stringingId);
        return stringingRepository.getStringing(stringingId);
    }

    public List<Stringing> getStringingsByStringerUserId(UUID stringerUserId) {
        log.info("Getting stringings for stringer: {}", stringerUserId);
        return stringingRepository.getStringingsByStringerUserId(stringerUserId);
    }

    public List<Stringing> getStringingsByOwnerUserId(UUID ownerUserId) {
        log.info("Getting stringings for owner: {}", ownerUserId);
        // Owner index items are sparse (only contain stringingId for GSI lookups)
        // We need to fetch full details for each stringing
        List<Stringing> sparseItems = stringingRepository.getStringingsByOwnerUserId(ownerUserId);
        return sparseItems.stream()
                .map(sparse -> stringingRepository.getStringing(sparse.getStringingId()))
                .filter(stringing -> stringing != null)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Updates an existing Stringing entity.
     *
     * <p><b>Update Strategy:</b></p>
     * <p>
     * Since the owner index item is sparse (only contains stringingId for GSI lookups),
     * most updates only need to modify the main DETAILS item. However, special handling
     * is required when:
     * <ul>
     *   <li><b>ownerUserId changes:</b> Must delete old owner index item and create new one</li>
     *   <li><b>stringerUserId changes:</b> Must update gsiPk on main item</li>
     *   <li><b>state changes:</b> Must update corresponding timestamp and validate transition</li>
     * </ul>
     * </p>
     *
     * @throws InvalidStateTransitionException if the requested state transition is invalid
     */
    public Stringing updateStringing(UUID stringingId, UpdateStringingRequest request) {
        log.info("Updating stringing {} with request: {}", stringingId, request);

        Stringing existing = stringingRepository.getStringing(stringingId);
        if (existing == null) {
            log.warn("Stringing not found: {}", stringingId);
            return null;
        }

        StringingState currentState = existing.getState();

        if (currentState.isFinalState()) {
            log.warn("Cannot update stringing {} - already in final state: {}", stringingId, currentState);
            throw new InvalidStateTransitionException(
                    String.format("Stringing is in final state %s and cannot be modified", currentState));
        }

        if (request.getState() != null && request.getState() != currentState) {
            if (!currentState.canTransitionTo(request.getState())) {
                log.warn("Invalid state transition requested: {} -> {}", currentState, request.getState());
                throw new InvalidStateTransitionException(currentState, request.getState());
            }
        }

        UUID oldOwnerUserId = existing.getOwnerUserId();
        UUID oldStringerUserId = existing.getStringerUserId();
        Instant now = Instant.now();

        if (request.getStringerUserId() != null) {
            existing.setStringerUserId(request.getStringerUserId());
            existing.setGsiPk(Stringing.createGsiStringerPk(request.getStringerUserId()));
            existing.setGsiSk(Stringing.createGsiSk(stringingId));
        }

        if (request.getOwnerUserId() != null) {
            existing.setOwnerUserId(request.getOwnerUserId());
        }

        if (request.getRacketMake() != null) {
            existing.setRacketMake(request.getRacketMake());
        }

        if (request.getRacketModel() != null) {
            existing.setRacketModel(request.getRacketModel());
        }

        if (request.getStringType() != null) {
            existing.setStringType(request.getStringType());
        }

        if (request.getStringColor() != null) {
            existing.setStringColor(request.getStringColor());
        }

        if (request.getMainsTensionLbs() != null) {
            existing.setMainsTensionLbs(request.getMainsTensionLbs());
        }

        if (request.getCrossesTensionLbs() != null) {
            existing.setCrossesTensionLbs(request.getCrossesTensionLbs());
        }

        if (request.getState() != null && request.getState() != existing.getState()) {
            existing.setState(request.getState());
            updateStateTimestamp(existing, request.getState(), now);
        }

        stringingRepository.updateStringing(existing);

        boolean ownerChanged = request.getOwnerUserId() != null &&
                !Objects.equals(oldOwnerUserId, request.getOwnerUserId());

        if (ownerChanged) {
            if (oldOwnerUserId != null) {
                stringingRepository.deleteOwnerIndexItem(stringingId, oldOwnerUserId);
            }

            Stringing newOwnerIndexItem = new Stringing();
            newOwnerIndexItem.setPK(Stringing.createPk(stringingId));
            newOwnerIndexItem.setSK(Stringing.createSkOwner(request.getOwnerUserId()));
            newOwnerIndexItem.setStringingId(stringingId);
            newOwnerIndexItem.setGsiPk(Stringing.createGsiOwnerPk(request.getOwnerUserId()));
            newOwnerIndexItem.setGsiSk(Stringing.createGsiSk(stringingId));
            stringingRepository.saveStringing(newOwnerIndexItem);
        }

        log.info("Successfully updated stringing: {}", existing);
        return existing;
    }

    private void updateStateTimestamp(Stringing stringing, StringingState newState, Instant timestamp) {
        switch (newState) {
            case REQUESTED_BUT_NOT_DELIVERED -> stringing.setRequestedAt(timestamp);
            case DECLINED -> stringing.setDeclinedAt(timestamp);
            case RECEIVED_BUT_NOT_STARTED -> stringing.setReceivedAt(timestamp);
            case IN_PROGRESS -> stringing.setInProgressAt(timestamp);
            case FINISHED_BUT_NOT_PICKED_UP -> stringing.setFinishedAt(timestamp);
            case FAILED_BUT_NOT_PICKED_UP -> stringing.setFailedAt(timestamp);
            case COMPLETED -> stringing.setCompletedAt(timestamp);
            case FAILED_COMPLETED -> stringing.setFailedCompletedAt(timestamp);
        }
    }
}
