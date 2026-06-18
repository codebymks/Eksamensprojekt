# CLAUDE.md — SeismicMonitor

Guidance for working on the SeismicMonitor 24-hour exam project.

## What this project is

A full-stack REST web application for monitoring earthquakes. Seismic sensors
(simulated by a Docker container) send raw readings. The system stores them and,
when a single request contains exactly three valid readings, estimates an
epicenter and magnitude and creates an earthquake alert. Users can view active
alerts and submit reports; admins review readings and manage alert status.

## Tech stack

- Backend: Spring Boot (Spring Web, Spring Data JPA, Validation)
- Database:  MySQL 
- Security: Spring Security
- Tests: JUnit
- Frontend: HTML, CSS, JavaScript (talks to backend via REST, uses `fetch`)

## Core principle

Build one feature fully through the stack (DB → REST → working button on the
frontend) before starting the next. A working vertical slice beats many
half-finished pages. Keep it simple — do not add features beyond the assignment. Code frontend as simple as possible, make it beginner friendly.

Wherever the assignment says a calculation must be swappable, hide it behind an
interface: `EpicenterEstimator`, `MagnitudeEstimator`, `GeocodingService`.

## Model (entities)

Only necessary attributes are listed. Do not add extra fields.

### Sensor
- `id` (PK)
- `sensorId` (from JSON)
- `latitude`
- `longitude`

CRUD: Create (find-or-create on incoming data — never duplicate an existing
sensor), Read (via raw readings). No update/delete.

### SensorReading
- `id` (PK, generated)
- `readingId` (from JSON)
- `estimatedDistanceToEpicenterKm`
- `estimatedMagnitude`
- `recordedAt`
- `sensor` (`@ManyToOne`)
- `alert` (`@ManyToOne`, nullable)

CRUD: Create (save every valid reading, even when no alert is made), Read (list
raw readings; readings that led to an alert). No update/delete.

### EarthquakeAlert
- `id` (PK)
- `epicenterLatitude`
- `epicenterLongitude`
- `estimatedMagnitude`
- `status` (`AlertStatus`)
- `area` (set in delopgave 4 via reverse geocoding)
- `readings` (`@OneToMany`)
- `reports` (`@OneToMany`)

CRUD: Create (auto when exactly 3 valid readings arrive; status `UNDER_REVIEW`),
Read (active alerts, all alerts, single alert), Update (status only, enforce
allowed transitions). No delete.

### CitizenReport
- `id` (PK)
- `intensity`
- `alert` (`@ManyToOne`)

CRUD: Create (user submits a report for an alert; one per user per alert in
delopgave 5), Read (count per alert; reports for an alert). No update/delete.

### AlertStatus (enum)
- `UNDER_REVIEW`, `ACTIVE`, `FALSE_ALARM`, `NOT_ACTIVE`

Allowed transitions (enforce in the alert update logic):
- `UNDER_REVIEW → ACTIVE`
- `UNDER_REVIEW → FALSE_ALARM`
- `ACTIVE → NOT_ACTIVE`
- `FALSE_ALARM` and `NOT_ACTIVE` are final.

### AppUser (delopgave 5 only)
- `id` (PK)
- `username`
- `password` (hashed)
- `role` (USER / ADMIN)

CRUD: Read for authentication. Seed one USER and one ADMIN at startup.

## Business rules

- `POST /api/sensor-data` receives a list of 1–3 readings in one request.
- A reading is valid if: `sensorLocation` has latitude and longitude;
  `estimatedDistanceToEpicenterKm > 0`; `estimatedMagnitude > 0`; `recordedAt`
  parses as a date/time.
- Fewer than 3 readings: save the readings, create no alert.
- Exactly 3 valid readings: try to estimate epicenter + magnitude, create an
  alert with status `UNDER_REVIEW`, and link those 3 readings to it.
- If epicenter estimation fails: still save the readings, create no alert.
- Only combine readings within the same request, not across requests.

## Incoming JSON (POST /api/sensor-data)

Map to a request DTO, not directly to entities:

```json
[
  {
    "readingId": "READ-001",
    "sensorId": "SEN-001",
    "sensorLocation": { "latitude": 55.61880, "longitude": 11.13720 },
    "estimatedDistanceToEpicenterKm": 47.31,
    "estimatedMagnitude": 3.4,
    "recordedAt": "2026-05-20T10:15:30"
  }
]
```

## Implementation order (delopgaver)

1. Receive & store sensor data: entities Sensor + SensorReading, repositories,
   request DTO, `POST /api/sensor-data`, validation, `GET` raw readings.
2. Create alerts: EarthquakeAlert + AlertStatus, `EpicenterEstimator` and
   `MagnitudeEstimator` interfaces + impls, alert-creation rule, link readings.
3. Alerts + citizen reports: CitizenReport, endpoints for active/all alerts,
   create report, report counts/list, status change (enforce transitions),
   readings per alert; simple frontend.
4. Reverse geocoding: `GeocodingService` interface + Nominatim impl, populate
   `area` when an alert is created.
5. Security: AppUser + roles, secure endpoints (USER vs ADMIN), keep
   `POST /api/sensor-data` public.

## Testing

Unit-test the central business rules: validation, the 3-valid-readings →
alert rule, "estimation fails → readings saved, no alert", and the status
transition rules.

## Public endpoint

`POST /api/sensor-data` must stay public so the Docker container can post
without logging in. Everything else is role-protected once delopgave 5 is done.

## Running the sensor simulator

```bash
docker compose up -d        # starts sending readings every 15s to localhost:8080
docker compose logs sensors # check simulated readings / connection errors
```

App must run on `localhost:8080`.
