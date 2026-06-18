package org.example.exam.service;

import org.example.exam.dto.SensorReadingDTO;
import org.example.exam.model.Sensor;
import org.example.exam.model.SensorReading;
import org.example.exam.repository.SensorReadingRepository;
import org.example.exam.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//Assigment 1
@Service
public class SensorReadingService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    //Checks that a reading has a location, positive distance/magnitude, and a parsed timestamp.
    private boolean isValid(SensorReadingDTO r) {
        return r.sensorLocation() != null
                && r.estimatedDistanceToEpicenterKm() > 0
                && r.estimatedMagnitude() > 0
                && r.recordedAt() != null;
    }

    //Saves every valid reading, finding or creating its sensor by sensorId.
    public void receive(List<SensorReadingDTO> readings) {
        for (SensorReadingDTO dto : readings) {
            if (!isValid(dto)) continue;

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
            reading.setRecordedAt(dto.recordedAt());
            reading.setSensor(sensor);
            sensorReadingRepository.save(reading);
        }
    }

    //Returns all sensor readings.
    public List<SensorReading> getAllReadings() {
        return sensorReadingRepository.findAll();
    }
}