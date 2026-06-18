package org.example.exam.controller;

import org.example.exam.dto.CitizenReportDTO;
import org.example.exam.model.CitizenReport;
import org.example.exam.model.EarthquakeAlert;
import org.example.exam.repository.CitizenReportRepository;
import org.example.exam.repository.EarthquakeAlertRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//Lets users submit a citizen report (felt intensity) for an existing earthquake alert.
@RestController
@RequestMapping("/api/alerts")
public class CitizenReportController {

    private final EarthquakeAlertRepository alertRepository;
    private final CitizenReportRepository citizenReportRepository;

    public CitizenReportController(EarthquakeAlertRepository alertRepository, CitizenReportRepository citizenReportRepository) {
        this.alertRepository = alertRepository;
        this.citizenReportRepository = citizenReportRepository;
    }

    //Creates a new citizen report for the given alert, if that alert exists.
    @PostMapping("/{id}/reports")
    public ResponseEntity<CitizenReport> submitReport(@PathVariable int id, @RequestBody CitizenReportDTO request) {
        EarthquakeAlert alert = alertRepository.findById(id).orElse(null);
        if (alert == null) {
            return ResponseEntity.notFound().build();
        }

        CitizenReport report = new CitizenReport();
        report.setIntensity(request.intensity());
        report.setAlert(alert);
        citizenReportRepository.save(report);

        return ResponseEntity.ok(report);
    }

    //Returns every citizen report submitted for one alert, so admins can see each reported intensity.
    @GetMapping("/{id}/reports")
    public List<CitizenReport> getReports(@PathVariable int id) {
        return citizenReportRepository.findByAlertId(id);
    }
}
