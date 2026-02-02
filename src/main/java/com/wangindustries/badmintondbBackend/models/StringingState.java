package com.wangindustries.badmintondbBackend.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the lifecycle states of a stringing job.
 *
 * <p><b>State Transition Diagram:</b></p>
 * <pre>
 * REQUESTED_BUT_NOT_DELIVERED
 *       ↙     ↓      ↘
 *  CANCELED  DECLINED  RECEIVED_BUT_NOT_STARTED
 *                              ↓
 *                         IN_PROGRESS
 *                            ↙   ↘
 *        FINISHED_BUT_NOT_PICKED_UP    FAILED_BUT_NOT_PICKED_UP
 *                  ↓                              ↓
 *             COMPLETED                    FAILED_COMPLETED
 * </pre>
 *
 * <p>Once a stringing reaches COMPLETED, FAILED_COMPLETED, DECLINED, or CANCELED, no further state changes are allowed.</p>
 * <p>DECLINED = stringer declined the request, CANCELED = owner canceled the request</p>
 */
public enum StringingState {
    REQUESTED_BUT_NOT_DELIVERED,
    CANCELED,
    DECLINED,
    RECEIVED_BUT_NOT_STARTED,
    IN_PROGRESS,
    FINISHED_BUT_NOT_PICKED_UP,
    FAILED_BUT_NOT_PICKED_UP,
    COMPLETED,
    FAILED_COMPLETED;

    private static final Set<StringingState> FINAL_STATES = Set.of(COMPLETED, FAILED_COMPLETED, DECLINED, CANCELED);

    private static final Map<StringingState, Set<StringingState>> VALID_TRANSITIONS;

    static {
        VALID_TRANSITIONS = new HashMap<>();
        VALID_TRANSITIONS.put(REQUESTED_BUT_NOT_DELIVERED, Set.of(RECEIVED_BUT_NOT_STARTED, DECLINED, CANCELED));
        VALID_TRANSITIONS.put(CANCELED, Set.of());
        VALID_TRANSITIONS.put(DECLINED, Set.of());
        VALID_TRANSITIONS.put(RECEIVED_BUT_NOT_STARTED, Set.of(IN_PROGRESS));
        VALID_TRANSITIONS.put(IN_PROGRESS, Set.of(FINISHED_BUT_NOT_PICKED_UP, FAILED_BUT_NOT_PICKED_UP));
        VALID_TRANSITIONS.put(FINISHED_BUT_NOT_PICKED_UP, Set.of(COMPLETED));
        VALID_TRANSITIONS.put(FAILED_BUT_NOT_PICKED_UP, Set.of(FAILED_COMPLETED));
        VALID_TRANSITIONS.put(COMPLETED, Set.of());
        VALID_TRANSITIONS.put(FAILED_COMPLETED, Set.of());
    }

    public boolean canTransitionTo(StringingState newState) {
        Set<StringingState> validNextStates = VALID_TRANSITIONS.get(this);
        return validNextStates != null && validNextStates.contains(newState);
    }

    public boolean isFinalState() {
        return FINAL_STATES.contains(this);
    }
}
