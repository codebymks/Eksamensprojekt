package org.example.exam.service;

import org.springframework.stereotype.Service;

import java.util.List;

//Assignment 2
@Service
public class MagnitudeEstimatorImpl implements MagnitudeEstimator {

    //Averages the reported magnitudes.
    @Override
    public double estimate(List<Double> magnitudes) {
        if (magnitudes.isEmpty()) {
            throw new IllegalArgumentException("Der skal være mindst én måling.");
        }

        double sum = 0;
        for (double magnitude : magnitudes) {
            sum += magnitude;
        }

        return sum / magnitudes.size();
    }
}
