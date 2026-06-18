package org.example.exam.repository;

import org.example.exam.enums.AlertStatus;
import org.example.exam.model.EarthquakeAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Assignment 3
public interface EarthquakeAlertRepository extends JpaRepository<EarthquakeAlert, Integer> {
    List<EarthquakeAlert> findByAlertStatus(AlertStatus alertStatus);
}
