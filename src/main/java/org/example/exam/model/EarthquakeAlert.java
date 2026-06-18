package org.example.exam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

//An earthquake alert: the estimated epicenter/magnitude for a quake, plus the readings and reports linked to it.
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

    //Ignored when this alert is sent as JSON: SensorReading also links back to its alert,
    //and serializing both directions at once would recurse forever.
    @JsonIgnore
    @OneToMany(mappedBy = "alert")
    private List<SensorReading> sensorReadings;

    @JsonIgnore
    @OneToMany(mappedBy = "alert")
    private List<CitizenReport> citizenReports;

    //How many citizen reports this alert has received, shown to admins without fetching the full list.
    public int getReportCount() {
        return citizenReports.size();
    }
}