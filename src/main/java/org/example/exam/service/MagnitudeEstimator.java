package org.example.exam.service;

import java.util.List;

//Assignment 2
//Defines how to turn the magnitude values reported by individual sensors into one overall magnitude, so the calculation behind it can be swapped out.
public interface MagnitudeEstimator {

    //Estimates the overall magnitude from the magnitude values reported by the sensors.
    double estimate(List<Double> magnitudes);
}