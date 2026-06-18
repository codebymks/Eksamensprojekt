package org.example.exam.dto;

import java.time.LocalDateTime;
//assignment 1
public record SensorReadingDTO(
        String readingId,
        String sensorId,
        SensorLocationDTO sensorLocation,
        double estimatedDistanceToEpicenterKm,
        double estimatedMagnitude,
        LocalDateTime recordedAt
) {}