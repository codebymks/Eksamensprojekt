package org.example.exam.service;

import org.example.exam.dto.SensorLocationDTO;
import org.example.exam.dto.SensorReadingDTO;
import org.example.exam.repository.EarthquakeAlertRepository;
import org.example.exam.repository.SensorReadingRepository;
import org.example.exam.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SensorDataServiceTest {

    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private SensorReadingRepository sensorReadingRepository;
    @Mock
    private EarthquakeAlertRepository earthquakeAlertRepository;
    @Mock
    private EpicenterEstimator epicenterEstimator;
    @Mock
    private MagnitudeEstimator magnitudeEstimator;
    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private SensorDataService sensorDataService;

    //Sending 3 valid sensor readings should create 1 earthquake alert.
    @Test
    void threeValidReadings_createsOneAlert() {
        //Arrange: pretend the sensors are new, and give the estimators simple fake answers.
        when(sensorRepository.findBySensorID(any())).thenReturn(Optional.empty());
        when(sensorRepository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(sensorReadingRepository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(epicenterEstimator.estimate(any())).thenReturn(new EpicenterEstimator.Location(55.6, 11.2));
        when(magnitudeEstimator.estimate(any())).thenReturn(4.0);

        SensorLocationDTO location = new SensorLocationDTO(55.0, 11.0);
        SensorReadingDTO reading1 = new SensorReadingDTO("R1", "S1", location, 50.0, 4.0, "2026-05-20T10:15:30");
        SensorReadingDTO reading2 = new SensorReadingDTO("R2", "S2", location, 50.0, 4.0, "2026-05-20T10:15:30");
        SensorReadingDTO reading3 = new SensorReadingDTO("R3", "S3", location, 50.0, 4.0, "2026-05-20T10:15:30");

        //Act: send all 3 readings in one request.
        sensorDataService.receive(List.of(reading1, reading2, reading3));

        //Assert: exactly one earthquake alert was saved.
        verify(earthquakeAlertRepository).save(any());
    }
}
