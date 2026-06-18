package org.example.exam.repository;

import org.example.exam.model.CitizenReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//Spring Data JPA repository for CitizenReport.
public interface CitizenReportRepository extends JpaRepository<CitizenReport, Integer> {
    //Finds all citizen reports submitted for one alert (used by the admin reports view).
    List<CitizenReport> findByAlertId(int alertId);
}
