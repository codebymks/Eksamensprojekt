package org.example.exam.service;

import org.springframework.stereotype.Service;

import java.util.List;
//Assignment 2
//Estimates the epicenter using trilateration: three distance circles around sensors intersect at one point.
@Service
public class EpicenterEstimatorImpl implements EpicenterEstimator {

    //Calculates the epicenter location from three location+distance measurements using trilateration.
    @Override
    public Location estimate(List<LocationWithDistance> measurements) {
        if (measurements.size() < 3) {
            throw new IllegalArgumentException(
                    "Der skal være mindst tre målinger."
            );
        }

        Location ref = measurements.getFirst().location();
        double refLatRad = Math.toRadians(ref.latitude());
        double refLonRad = Math.toRadians(ref.longitude());
        double earthRadius = 6371.00; // KM

        double[] x = new double[3];
        double[] y = new double[3];
        double[] d = new double[3];

        for (int i = 0; i < 3; i++) {
            Location loc = measurements.get(i).location();
            double latRad = Math.toRadians(loc.latitude());
            double lonRad = Math.toRadians(loc.longitude());
            x[i] = earthRadius * (lonRad - refLonRad) * Math.cos(refLatRad);
            y[i] = earthRadius * (latRad - refLatRad);
            d[i] = measurements.get(i).distance(); // km
        }

        double A = 2 * (x[1] - x[0]);
        double B = 2 * (y[1] - y[0]);
        double C = d[0] * d[0] - d[1] * d[1] - x[0] * x[0] + x[1] * x[1]
                - y[0] * y[0] + y[1] * y[1];

        double D = 2 * (x[2] - x[1]);
        double E = 2 * (y[2] - y[1]);
        double F = d[1] * d[1] - d[2] * d[2] - x[1] * x[1] + x[2] * x[2]
                - y[1] * y[1] + y[2] * y[2];

        double denominator = A * E - B * D;
        if (Math.abs(denominator) < 1e-12) {
            throw new IllegalArgumentException(
                    "Målepunkterne giver ingen stabil løsning."
            );
        }

        double epicenterX = (C * E - B * F) / denominator;
        double epicenterY = (A * F - C * D) / denominator;

        double epicenterLatRad = refLatRad + epicenterY / earthRadius;
        double epicenterLonRad = refLonRad + epicenterX / (earthRadius * Math.cos(refLatRad));

        return new Location(
                Math.toDegrees(epicenterLatRad),
                Math.toDegrees(epicenterLonRad)
        );
    }
}
