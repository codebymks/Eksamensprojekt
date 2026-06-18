package org.example.exam.controller;

import org.example.exam.dto.AlertStatusUpdateDTO;
import org.example.exam.enums.AlertStatus;
import org.example.exam.model.EarthquakeAlert;
import org.example.exam.model.SensorReading;
import org.example.exam.repository.EarthquakeAlertRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//Assignment 3
//Earthquake alerts for the frontend to read and (for admins) update.
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final EarthquakeAlertRepository repository;

    public AlertController(EarthquakeAlertRepository repository) {
        this.repository = repository;
    }

    //Returns all alerts that are currently ACTIVE. Used for user page.
    @GetMapping("/active")
    public List<EarthquakeAlert> getActiveAlerts() {
        return repository.findByAlertStatus(AlertStatus.ACTIVE);
    }

    //Returns every alert, regardless of status, used for the admin page.
    @GetMapping
    public List<EarthquakeAlert> getAllAlerts() {
        return repository.findAll();
    }

    //Changes an alert's status, but only if it's an allowed transition from its current status.
    @PatchMapping("/{id}/status")
    public ResponseEntity<EarthquakeAlert> updateStatus(@PathVariable int id, @RequestBody AlertStatusUpdateDTO request) {
        EarthquakeAlert alert = repository.findById(id).orElse(null);
        if (alert == null) {
            return ResponseEntity.notFound().build();
        }

        if (!alert.getAlertStatus().canTransitionTo(request.status())) {
            return ResponseEntity.badRequest().build();
        }

        alert.setAlertStatus(request.status());
        repository.save(alert);
        return ResponseEntity.ok(alert);
    }

    //Returns the exact sensor readings that led to this alert, for admin.
    @GetMapping("/{id}/readings")
    public ResponseEntity<List<SensorReading>> getReadings(@PathVariable int id) {
        EarthquakeAlert alert = repository.findById(id).orElse(null);
        if (alert == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(alert.getSensorReadings());
    }
}
