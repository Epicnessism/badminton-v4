package com.wangindustries.badmintondbBackend.models;

import java.util.Map;
import java.util.Set;

/**
 * Represents the lifecycle states of a stringing job.
 *
 * <p><b>State Transition Diagram:</b></p>
 * <pre>
 * REQUESTED_BUT_NOT_DELIVERED
 *         ↙   ↘
 *    DECLINED   RECEIVED_BUT_NOT_STARTED
 *                        ↓
 *                   IN_PROGRESS
 *                      ↙   ↘
 *      FINISHED_BUT_NOT_PICKED_UP    FAILED_BUT_NOT_PICKED_UP
 *                ↓                              ↓
 *           COMPLETED                    FAILED_COMPLETED
 * </pre>
 *
 * <p>Once a stringing reaches COMPLETED, FAILED_COMPLETED, or DECLINED, no further state changes are allowed.</p>
 */
public enum StringingState {
    REQUESTED_BUT_NOT_DELIVERED,
    DECLINED,
    RECEIVED_BUT_NOT_STARTED,
    IN_PROGRESS,
    FINISHED_BUT_NOT_PICKED_UP,
    FAILED_BUT_NOT_PICKED_UP,
    COMPLETED,
    FAILED_COMPLETED;

    private static final Set<StringingState> FINAL_STATES = Set.of(COMPLETED, FAILED_COMPLETED, DECLINED);

    private static final Map<StringingState, Set<StringingState>> VALID_TRANSITIONS = Map.of(
            REQUESTED_BUT_NOT_DELIVERED, Set.of(RECEIVED_BUT_NOT_STARTED, DECLINED),
            DECLINED, Set.of(),
            RECEIVED_BUT_NOT_STARTED, Set.of(IN_PROGRESS),
            IN_PROGRESS, Set.of(FINISHED_BUT_NOT_PICKED_UP, FAILED_BUT_NOT_PICKED_UP),
            FINISHED_BUT_NOT_PICKED_UP, Set.of(COMPLETED),
            FAILED_BUT_NOT_PICKED_UP, Set.of(FAILED_COMPLETED),
            COMPLETED, Set.of(),
            FAILED_COMPLETED, Set.of()
    );

    public boolean canTransitionTo(StringingState newState) {
        Set<StringingState> validNextStates = VALID_TRANSITIONS.get(this);
        return validNextStates != null && validNextStates.contains(newState);
    }

    public boolean isFinalState() {
        return FINAL_STATES.contains(this);
    }
}
