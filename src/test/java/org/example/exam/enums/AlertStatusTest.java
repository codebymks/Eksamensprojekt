package org.example.exam.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Checks that AlertStatus only allows the status changes listed in the assignment.
class AlertStatusTest {

    //UNDER_REVIEW must be allowed to move to ACTIVE or FALSE_ALARM.
    @Test
    void underReviewCanMoveToActiveOrFalseAlarm() {
        assertTrue(AlertStatus.UNDER_REVIEW.canTransitionTo(AlertStatus.ACTIVE));
        assertTrue(AlertStatus.UNDER_REVIEW.canTransitionTo(AlertStatus.FALSE_ALARM));
    }

    //ACTIVE must be allowed to move to NOT_ACTIVE, but not back to UNDER_REVIEW.
    @Test
    void activeCanOnlyMoveToNotActive() {
        assertTrue(AlertStatus.ACTIVE.canTransitionTo(AlertStatus.NOT_ACTIVE));
        assertFalse(AlertStatus.ACTIVE.canTransitionTo(AlertStatus.UNDER_REVIEW));
    }

    //FALSE_ALARM and NOT_ACTIVE are final statuses, so no further moves are allowed.
    @Test
    void falseAlarmAndNotActiveAreFinal() {
        assertFalse(AlertStatus.FALSE_ALARM.canTransitionTo(AlertStatus.ACTIVE));
        assertFalse(AlertStatus.NOT_ACTIVE.canTransitionTo(AlertStatus.ACTIVE));
    }
}
