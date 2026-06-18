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

//Assignment 1
//Receives sensor readings, saves them, and creates an alert when exactly 3 valid readings arrive together.
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

    //Saves every valid reading, then creates an alert if exactly 3 valid readings arrived in this request.
    @Transactional
    public void receive(List<SensorReadingDTO> readings) {
        List<SensorReading> validReadings = new ArrayList<>();

        for (SensorReadingDTO dto : readings) {
            SensorReading reading = saveReading(dto);
            if (isValid(dto)) {
                validReadings.add(reading);
            }
        }

        if (validReadings.size() == 3) {
            tryCreateAlert(validReadings);
        }
    }

    //Returns all sensor readings.
    public List<SensorReading> getAllReadings() {
        return sensorReadingRepository.findAll();
    }

    //Checks that a reading has a location, positive distance and magnitude, and a valid date/time.
    private boolean isValid(SensorReadingDTO dto) {
        return dto.sensorLocation() != null
                && dto.estimatedDistanceToEpicenterKm() > 0
                && dto.estimatedMagnitude() > 0
                && parseRecordedAt(dto.recordedAt()) != null;
    }

    //Saves one reading, linking it to its sensor (found by sensorId, or created if new).
    private SensorReading saveReading(SensorReadingDTO dto) {
        Sensor sensor = findOrCreateSensor(dto);

        SensorReading reading = new SensorReading();
        reading.setReadingId(dto.readingId());
        reading.setEstimatedDistanceToEpicenterKm(dto.estimatedDistanceToEpicenterKm());
        reading.setEstimatedMagnitude(dto.estimatedMagnitude());
        reading.setRecordedAt(parseRecordedAt(dto.recordedAt()));
        reading.setSensor(sensor);
        return sensorReadingRepository.save(reading);
    }

    //Finds the sensor by its sensorId, or creates and saves a new one if it does not exist yet.
    private Sensor findOrCreateSensor(SensorReadingDTO dto) {
        Sensor existing = sensorRepository.findBySensorID(dto.sensorId()).orElse(null);
        if (existing != null) {
            return existing;
        }
        Sensor sensor = new Sensor();
        sensor.setSensorID(dto.sensorId());
        sensor.setLatitude(dto.sensorLocation().latitude());
        sensor.setLongitude(dto.sensorLocation().longitude());
        return sensorRepository.save(sensor);
    }

    //Parses recordedAt into a LocalDateTime, or returns null if it isn't a valid date/time.
    private LocalDateTime parseRecordedAt(String recordedAt) {
        if (recordedAt == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(recordedAt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    //Estimates epicenter and magnitude from the 3 readings, looks up the area, and creates an UNDER_REVIEW alert.
    //If epicenter estimation fails, the readings stay saved but no alert is created.
    private void tryCreateAlert(List<SensorReading> threeReadings) {
        EpicenterEstimator.Location epicenter = estimateEpicenter(threeReadings);
        if (epicenter == null) {
            return;
        }

        double magnitude = estimateMagnitude(threeReadings);
        String area = geocodingService.reverseGeocode(epicenter.latitude(), epicenter.longitude());

        EarthquakeAlert alert = new EarthquakeAlert();
        alert.setEpicenterLatitude(epicenter.latitude());
        alert.setEpicenterLongitude(epicenter.longitude());
        alert.setEstimatedMagnitude(magnitude);
        alert.setAlertStatus(AlertStatus.UNDER_REVIEW);
        alert.setArea(area);
        earthquakeAlertRepository.save(alert);

        linkReadingsToAlert(threeReadings, alert);
    }

    //Runs the epicenter estimator on the readings; returns null if it cannot find a stable solution.
    private EpicenterEstimator.Location estimateEpicenter(List<SensorReading> readings) {
        List<EpicenterEstimator.LocationWithDistance> measurements = new ArrayList<>();
        for (SensorReading reading : readings) {
            EpicenterEstimator.Location sensorLocation = new EpicenterEstimator.Location(
                    reading.getSensor().getLatitude(), reading.getSensor().getLongitude());
            measurements.add(new EpicenterEstimator.LocationWithDistance(
                    sensorLocation, reading.getEstimatedDistanceToEpicenterKm()));
        }
        try {
            return epicenterEstimator.estimate(measurements);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    //Combines the readings' magnitudes into a single estimated magnitude.
    private double estimateMagnitude(List<SensorReading> readings) {
        List<Double> magnitudes = new ArrayList<>();
        for (SensorReading reading : readings) {
            magnitudes.add(reading.getEstimatedMagnitude());
        }
        return magnitudeEstimator.estimate(magnitudes);
    }

    //Links each reading to the alert so an admin can later see which readings triggered it.
    private void linkReadingsToAlert(List<SensorReading> readings, EarthquakeAlert alert) {
        for (SensorReading reading : readings) {
            reading.setAlert(alert);
            sensorReadingRepository.save(reading);
        }
    }
}
