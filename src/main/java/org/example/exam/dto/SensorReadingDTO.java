package org.example.exam.dto;

//assignment 1
public record SensorReadingDTO(
        String readingId,
        String sensorId,
        SensorLocationDTO sensorLocation,
        double estimatedDistanceToEpicenterKm,
        double estimatedMagnitude,
        String recordedAt
) {}