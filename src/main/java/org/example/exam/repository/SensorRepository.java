package org.example.exam.repository;

import org.example.exam.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Assigment 1
public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findBySensorID(String sensorID);
}
