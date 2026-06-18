package org.example.exam.dto;

import org.example.exam.enums.AlertStatus;
//Assignment 3
//Request body for changing an alert's status: just the new status to move to.
public record AlertStatusUpdateDTO(AlertStatus status) {
}