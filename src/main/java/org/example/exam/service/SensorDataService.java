package org.example.exam.service;

import org.example.exam.dto.SensorReadingDTO;
import org.example.exam.enums.AlertStatus;
import org.example.exam.model.EarthquakeAlert;
import org.example.exam.model.Sensor;
import org.example.exam.model.SensorReading;
import org.example.exam.repository.EarthquakeAlertRepository;
import org.example.exam.repository.SensorReadingRepository;
import org.example.exam.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

//Assigment 1
@Service
public class SensorDataService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private EarthquakeAlertRepository earthquakeAlertRepository;

    @Autowired
    private EpicenterEstimator epicenterEstimator;

    @Autowired
    private MagnitudeEstimator magnitudeEstimator;

    @Autowired
    private GeocodingService geocodingService;

    //Checks if a sensorReadingDTO is valid.
    private boolean isValid(SensorReadingDTO r) {
        //SensorLocation has latitude and longitude
        return r.sensorLocation() != null
                //is over 0
                && r.estimatedDistanceToEpicenterKm() > 0
                && r.estimatedMagnitude() > 0
                //is a valid date/time
                && parseRecordedAt(r.recordedAt()) != null;
    }

    //Parses recordedAt into a LocalDateTime, or returns null if it isn't a valid date/time.
    private LocalDateTime parseRecordedAt(String recordedAt) {
        if (recordedAt == null) return null;
        try {
            return LocalDateTime.parse(recordedAt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    //Saves every valid reading, finding or creating its sensor by sensorId, then creates an alert if exactly 3 valid readings arrived in this request.
    @Transactional
    public void receive(List<SensorReadingDTO> readings) {
        List<SensorReading> validReadings = new ArrayList<>();

        for (SensorReadingDTO dto : readings) {

            Sensor sensor = sensorRepository.findBySensorID(dto.sensorId())
                    .orElseGet(() -> {
                        Sensor newSensor = new Sensor();
                        newSensor.setSensorID(dto.sensorId());
                        newSensor.setLatitude(dto.sensorLocation().latitude());
                        newSensor.setLongitude(dto.sensorLocation().longitude());
                        return sensorRepository.save(newSensor);
                    });

            SensorReading reading = new SensorReading();
            reading.setReadingId(dto.readingId());
            reading.setEstimatedDistanceToEpicenterKm(dto.estimatedDistanceToEpicenterKm());
            reading.setEstimatedMagnitude(dto.estimatedMagnitude());
            reading.setRecordedAt(parseRecordedAt(dto.recordedAt()));
            reading.setSensor(sensor);
            sensorReadingRepository.save(reading);

            if (isValid(dto)) {
                validReadings.add(reading);
            }
        }

        if (validReadings.size() == 3) {
            tryCreateAlert(validReadings);
        }
    }

    //Assignment 4
    //Estimates the epicenter and magnitude from exactly 3 readings, reverse-geocodes the epicenter into an area name, and creates an UNDER_REVIEW alert linked to them; if epicenter estimation fails, no alert is created.
    private void tryCreateAlert(List<SensorReading> threeReadings) {
        List<EpicenterEstimator.LocationWithDistance> measurements = threeReadings.stream()
                .map(r -> new EpicenterEstimator.LocationWithDistance(
                        new EpicenterEstimator.Location(r.getSensor().getLatitude(), r.getSensor().getLongitude()),
                        r.getEstimatedDistanceToEpicenterKm()))
                .toList();

        EpicenterEstimator.Location epicenter;
        try {
            epicenter = epicenterEstimator.estimate(measurements);
        } catch (IllegalArgumentException e) {
            return;
        }

        double magnitude = magnitudeEstimator.estimate(
                threeReadings.stream().map(SensorReading::getEstimatedMagnitude).toList());

        EarthquakeAlert alert = new EarthquakeAlert();
        alert.setEpicenterLatitude(epicenter.latitude());
        alert.setEpicenterLongitude(epicenter.longitude());
        alert.setEstimatedMagnitude(magnitude);
        alert.setAlertStatus(AlertStatus.UNDER_REVIEW);
        alert.setArea(geocodingService.reverseGeocode(epicenter.latitude(), epicenter.longitude()));
        earthquakeAlertRepository.save(alert);

        for (SensorReading reading : threeReadings) {
            reading.setAlert(alert);
            sensorReadingRepository.save(reading);
        }
    }

    //Returns all sensor readings.
    public List<SensorReading> getAllReadings() {
        return sensorReadingRepository.findAll();
    }
}