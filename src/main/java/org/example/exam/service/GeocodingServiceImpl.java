package org.example.exam.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

//Assignment 4
//Looks up the area name for a location using the OpenStreetMap (Nominatim) reverse geocoding API.
@Service
public class GeocodingServiceImpl implements GeocodingService {

    //Connection to the API. The User-Agent header is required by their usage policy.
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "SeismicMonitor/1.0")
            .build();

    //Returns an area name for the coordinates, or null if the lookup fails or finds nothing.
    @Override
    public String reverseGeocode(double latitude, double longitude) {
        OpenStreetAPIResponse response = callOpenStreetAPI(latitude, longitude);
        if (response == null || response.address() == null) {
            return null;
        }
        return response.address().areaName();
    }

    //Sends the reverse-geocoding request and reads the answer into our own simple objects. Returns null if the call fails.
    private OpenStreetAPIResponse callOpenStreetAPI(double latitude, double longitude) {
        try {
            return restClient.get()
                    .uri("/reverse?format=json&lat={lat}&lon={lon}", latitude, longitude)
                    .retrieve()
                    .body(OpenStreetAPIResponse.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    //The whole API answer. only keep the "address" part; other fields are ignored.
    private record OpenStreetAPIResponse(Address address) {}

    //The named places inside the address. list them from most specific (city) to least specific (state).
    private record Address(String city, String town, String village, String county, String state) {

        //Picks the most specific place name that is filled in, or null if none are.
        String areaName() {
            if (city != null) return city;
            if (town != null) return town;
            if (village != null) return village;
            if (county != null) return county;
            return state;
        }
    }
}
