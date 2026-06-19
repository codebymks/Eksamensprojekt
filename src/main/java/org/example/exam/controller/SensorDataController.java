package org.example.exam.controller;


import org.example.exam.dto.SensorReadingDTO;
import org.example.exam.model.SensorReading;
import org.example.exam.service.SensorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Assignment 1
//SHow sensor data and sensor reading data for both admin and user.
@RestController
@RequestMapping("/api")
public class SensorDataController {

    private final SensorDataService service;

    public SensorDataController(SensorDataService service) {
        this.service = service;
    }

    @PostMapping("/sensor-data")
    public ResponseEntity<Void> receive(@RequestBody List<SensorReadingDTO> readings) {
        service.receive(readings);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sensor-readings")
    public List<SensorReading> getReadings() {
        return service.getAllReadings();
    }
}