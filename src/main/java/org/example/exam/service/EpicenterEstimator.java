package org.example.exam.service;

import java.util.List;
//Assignment 2
//Defines how to turn at least three sensor measurements into an estimated epicenter location.
public interface EpicenterEstimator {

    //Estimates the epicenter location from a list of location+distance measurements.
    Location estimate(List<LocationWithDistance> measurements);

    record Location(double latitude, double longitude) {}

    record LocationWithDistance(Location location, double distance) {}
}
