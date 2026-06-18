package org.example.exam.dto;

//assignment 1
// recordedAt is kept as a raw String so an unparsable date fails validation for just this reading, instead of failing JSON deserialization for the whole request.
public record SensorReadingDTO(
        String readingId,
        String sensorId,
        SensorLocationDTO sensorLocation,
        double estimatedDistanceToEpicenterKm,
        double estimatedMagnitude,
        String recordedAt
) {}