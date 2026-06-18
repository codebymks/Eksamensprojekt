package org.example.exam.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.exam.enums.AlertStatus;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EarthquakeAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double epicenterLatitude;
    private double epicenterLongitude;
    private double estimatedMagnitude;

    @Enumerated(EnumType.STRING)
    private AlertStatus alertStatus;

    private String area;

    @OneToMany(mappedBy = "alert")
    private List<SensorReading> sensorReadings;

    @OneToMany(mappedBy = "alert")
    private List<CitizenReport> citizenReports;
}