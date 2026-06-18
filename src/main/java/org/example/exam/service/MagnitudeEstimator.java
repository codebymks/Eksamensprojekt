package org.example.exam.service;

import java.util.List;

//Assignment 2
//Defines how to turn the magnitude values reported by individual sensors into one overall magnitude.
public interface MagnitudeEstimator {

    double estimate(List<Double> magnitudes);
}