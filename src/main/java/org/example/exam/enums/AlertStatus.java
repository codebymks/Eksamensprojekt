package org.example.exam.enums;

//The status of an earthquake alert.
public enum AlertStatus {
    UNDER_REVIEW,
    ACTIVE,
    FALSE_ALARM,
    NOT_ACTIVE;

    //Checks if moving from this status to newStatus is one of the allowed transitions.
    public boolean canTransitionTo(AlertStatus newStatus) {
        return switch (this) {
            case UNDER_REVIEW -> newStatus == ACTIVE || newStatus == FALSE_ALARM;
            case ACTIVE -> newStatus == NOT_ACTIVE;
            case FALSE_ALARM, NOT_ACTIVE -> false;
        };
    }
}
