package org.example.exam.repository;

import org.example.exam.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

//Assignment 1
public interface SensorReadingRepository extends JpaRepository<SensorReading, Integer> {
}
