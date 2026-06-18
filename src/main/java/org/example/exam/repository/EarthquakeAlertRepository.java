package org.example.exam.repository;

import org.example.exam.model.EarthquakeAlert;
import org.springframework.data.jpa.repository.JpaRepository;

//Assignment 2
//Spring Data JPA repository for EarthquakeAlert.
public interface EarthquakeAlertRepository extends JpaRepository<EarthquakeAlert, Integer> {
}
