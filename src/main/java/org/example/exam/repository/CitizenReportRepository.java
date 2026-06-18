package org.example.exam.repository;

import org.example.exam.model.CitizenReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Assignment 3
public interface CitizenReportRepository extends JpaRepository<CitizenReport, Integer> {
    //Finds all citizen reports submitted for one alert (used by admin).
    List<CitizenReport> findByAlertId(int alertId);
}
