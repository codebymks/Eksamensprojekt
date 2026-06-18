package org.example.exam.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

//Assignment 4
//Looks up the area name for a location using the (OpenStreetMap) reverse geocoding API.
@Service
public class GeocodingServiceImpl implements GeocodingService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "SeismicMonitor/1.0")
            .build();

    //Calls url and picks the most specific address field as the area name; returns null if the lookup fails or finds nothing.
    @Override
    public String reverseGeocode(double latitude, double longitude) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/reverse?format=json&lat={lat}&lon={lon}", latitude, longitude)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null) {
                return null;
            }

            Object addressObj = response.get("address");
            if (!(addressObj instanceof Map<?, ?> address)) {
                return null;
            }

            for (String key : List.of("city", "town", "village", "county", "state")) {
                Object value = address.get(key);
                if (value != null) {
                    return value.toString();
                }
            }
            return null;
        } catch (RestClientException e) {
            return null;
        }
    }
}
