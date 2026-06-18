package org.example.exam.dto;

import org.example.exam.enums.AlertStatus;

//Request body for changing an alert's status: just the new status to move to.
public record AlertStatusUpdateDTO(AlertStatus status) {
}
