package org.example.exam.repository;

import org.example.exam.enums.AlertStatus;
import org.example.exam.model.EarthquakeAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Assignment 3
//Spring Data JPA repository for EarthquakeAlert.
public interface EarthquakeAlertRepository extends JpaRepository<EarthquakeAlert, Integer> {
    //Finds all alerts that currently have the given status (e.g. all ACTIVE alerts).
    List<EarthquakeAlert> findByAlertStatus(AlertStatus alertStatus);
}
