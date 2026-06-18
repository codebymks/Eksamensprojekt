package org.example.exam.service;

//Assignment 4
//Defines how to turn an epicenter location into a readable area name.
public interface GeocodingService {

    //Looks up the area name (e.g. city/town) for a given latitude/longitude. Returns null if no area can be found.
    String reverseGeocode(double latitude, double longitude);
}
