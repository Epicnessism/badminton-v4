package com.wangindustries.badmintondbBackend.exceptions;

import com.wangindustries.badmintondbBackend.models.StringingState;

public class InvalidStateTransitionException extends RuntimeException {

    private final StringingState currentState;
    private final StringingState requestedState;

    public InvalidStateTransitionException(StringingState currentState, StringingState requestedState) {
        super(String.format("Invalid state transition from %s to %s", currentState, requestedState));
        this.currentState = currentState;
        this.requestedState = requestedState;
    }

    public InvalidStateTransitionException(String message) {
        super(message);
        this.currentState = null;
        this.requestedState = null;
    }

    public StringingState getCurrentState() {
        return currentState;
    }

    public StringingState getRequestedState() {
        return requestedState;
    }
}
