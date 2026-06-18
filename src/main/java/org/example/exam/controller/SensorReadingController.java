package org.example.exam.controller;


import org.example.exam.dto.SensorReadingDTO;
import org.example.exam.model.SensorReading;
import org.example.exam.service.SensorReadingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Assignment 1
@RestController
@RequestMapping("/api")
public class SensorReadingController {

    private final SensorReadingService service;

    public SensorReadingController(SensorReadingService service) {
        this.service = service;
    }

    @PostMapping("/sensor-data")
    public ResponseEntity<Void> receive(@RequestBody List<SensorReadingDTO> readings) {
        System.out.println("🚀RECEIVED DATA:"+readings);
        service.receive(readings);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sensor-readings")
    public List<SensorReading> getReadings() {
        return service.getAllReadings();
    }
}