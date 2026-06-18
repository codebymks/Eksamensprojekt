package org.example.exam.service;

import org.springframework.stereotype.Service;

import java.util.List;

//Assignment 2
//Estimates the overall magnitude as the simple average of the magnitudes reported by the sensors.
@Service
public class MagnitudeEstimatorImpl implements MagnitudeEstimator {

    //Averages the reported magnitudes.
    @Override
    public double estimate(List<Double> magnitudes) {
        return magnitudes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Der skal være mindst én måling."));
    }
}
