# CLAUDE.md — SeismicMonitor

Guidance for working on the SeismicMonitor 24-hour exam project.

## What this project is

A full-stack REST web application for monitoring earthquakes. Seismic sensors (simulated by a Docker container) send raw readings. The system stores them and, when a single request contains exactly three valid readings, estimates an epicenter and magnitude and creates an earthquake alert. Users can view active alerts and submit reports; admins review readings and manage alert status.

## Tech stack

- Backend: Spring Boot (Spring Web, Spring Data JPA, Validation)
- Database:  MySQL
- Security: Spring Security
- Tests: JUnit
- Frontend: HTML, CSS, JavaScript (talks to backend via REST, uses `fetch`)

## Core principle

Build one feature fully through the stack (DB → REST → working button on the frontend) before starting the next. A working vertical slice beats many half-finished pages. Keep it simple — do not add features beyond the assignment. Code frontend as simple as possible, make it beginner friendly.

Wherever the assignment says a calculation must be swappable, hide it behind an interface: `EpicenterEstimator`, `MagnitudeEstimator`, `GeocodingService`.

## Code conventions

All code written by Claude must include an explanatory comment describing what it does:

- Every class gets a short comment above the class declaration explaining its responsibility (what it is for in the system).
- Every method gets a short comment above it explaining what the method does — its purpose, not a line-by-line restatement of the code.
- Keep comments concise and in plain language so they are beginner friendly. Update the comment if the behavior changes.

## Model (entities)

Only necessary attributes are listed. Do not add extra fields.

### Sensor
- `id` (PK)
- `sensorId` (from JSON)
- `latitude`
- `longitude`

CRUD: Create (find-or-create on incoming data — never duplicate an existing sensor), Read (via raw readings). No update/delete.

### SensorReading
- `id` (PK, generated)
- `readingId` (from JSON)
- `estimatedDistanceToEpicenterKm`
- `estimatedMagnitude`
- `recordedAt`
- `sensor` (`@ManyToOne`)
- `alert` (`@ManyToOne`, nullable)

CRUD: Create (save every valid reading, even when no alert is made), Read (list raw readings; readings that led to an alert). No update/delete.

### EarthquakeAlert
- `id` (PK)
- `epicenterLatitude`
- `epicenterLongitude`
- `estimatedMagnitude`
- `status` (`AlertStatus`)
- `area` (set in delopgave 4 via reverse geocoding)
- `readings` (`@OneToMany`)
- `reports` (`@OneToMany`)

CRUD: Create (auto when exactly 3 valid readings arrive; status `UNDER_REVIEW`), Read (active alerts, all alerts, single alert), Update (status only, enforce allowed transitions). No delete.

### CitizenReport
- `id` (PK)
- `intensity`
- `alert` (`@ManyToOne`)

CRUD: Create (user submits a report for an alert; one per user per alert in delopgave 5), Read (count per alert; reports for an alert). No update/delete.

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

## Delopgave 1 — Receive and store sensor data

Goal: implement the part of the system that receives raw readings from the simulated sensors (the Docker container) and stores them. No alert logic yet — this delopgave is only about getting data in and being able to read it back out.

The system must be able to:
- receive `POST /api/sensor-data`
- store sensors and sensor readings in the database
- handle requests with 1–3 readings
- save readings even when no earthquake alert can be created
- expose the raw sensor readings for viewing

How the pieces fit together:

1. **Request DTO.** The incoming JSON is a list of reading objects. Map it to a request DTO (e.g. `SensorDataRequest` / `SensorReadingDto`) rather than binding directly to JPA entities. The DTO mirrors the JSON shape, including the nested `sensorLocation` object with `latitude` and `longitude`. This keeps the API contract separate from the database model.

2. **Controller.** A `@RestController` with a `@PostMapping("/api/sensor-data")` that accepts the list of reading DTOs in the body and delegates to a service. It should accept a list of 1 to 3 readings. Return a sensible response (e.g. `200 OK`/`201 Created`); the container only needs the request to succeed.

3. **Find-or-create the sensor.** Each reading carries a `sensorId` and a `sensorLocation`. Before saving a reading, look up the sensor by its `sensorId`. If it already exists, reuse it; if not, create it. Never insert a duplicate sensor for the same `sensorId`. This is the "find-or-create" rule for the `Sensor` entity.

4. **Save every valid reading.** Persist each reading as a `SensorReading` linked to its sensor (`@ManyToOne`). At this stage the reading's `alert` is null — readings are stored regardless of whether they will ever lead to an alert. This is important: storage of raw data is never blocked by alert logic.

5. **Read back raw readings.** Provide a `GET` endpoint to list the stored raw readings so they can be inspected. (Restricting this to admins happens later in delopgave 5; for now just make it work.)

Done when: the running app accepts posts from the Docker simulator on `localhost:8080`, persists sensors and readings without duplicating sensors, and the stored readings can be listed via a `GET` endpoint.

## Delopgave 2 — Create earthquake alerts

Goal: when a single request contains exactly three valid readings, the system estimates an epicenter and a magnitude and creates an earthquake alert. This builds directly on delopgave 1 — the readings are still always stored; the alert is the new part.

The system must be able to:
- decide whether a request contains enough readings for an alert
- estimate the epicenter
- estimate the magnitude
- create an alert with status `UNDER_REVIEW`
- link the relevant sensor readings to the alert

### Decide whether there are enough readings

A single reading only gives a *distance* to the epicenter, not a *direction*. One distance describes a circle of possible epicenters around the sensor; two readings give two circles that usually cross in two points (still ambiguous); three circles intersect at a single point. That is why three readings are required to determine the epicenter's coordinates.

Validity must be checked before counting to three. A reading is valid if:
- `sensorLocation` contains both latitude and longitude
- `estimatedDistanceToEpicenterKm > 0`
- `estimatedMagnitude > 0`
- `recordedAt` parses as a date/time

Branching:
- Fewer than 3 readings in the request → save the readings, create no alert (the epicenter cannot be determined unambiguously).
- Exactly 3 valid readings → attempt to estimate epicenter and magnitude.

Only readings within the same request are combined — never across requests.

### Estimate the epicenter

The epicenter estimation is a provided "black box" algorithm (trilateration — the point where the three circles intersect). You do not need to understand its internal math, only call it. Two things matter:

- **It can fail.** For certain geometries (for example three points that are effectively collinear) the algorithm cannot produce a stable solution and throws. The rule is: if epicenter estimation fails, still save the readings but create no alert. So this call must be wrapped so a failure does not abort storing the readings and does not crash the whole request.
- **It must be swappable.** The solution should allow switching to a different calculation method without changing the calling code. Put the calculation behind an `EpicenterEstimator` interface and depend on the interface, not a concrete implementation.

The algorithm works on the three readings' sensor locations plus each reading's `estimatedDistanceToEpicenterKm`, and returns the estimated epicenter latitude and longitude.

### Estimate the magnitude

Combine the three readings' `estimatedMagnitude` values. A simple average is sufficient; a weighted average (readings closer to the epicenter weigh more) is an allowed alternative. Put this behind a `MagnitudeEstimator` interface for the same swap-without-changing-callers reason as the epicenter estimator.

### Create the alert with status UNDER_REVIEW

A new alert never starts visible or active. It is created with status `UNDER_REVIEW` because an admin must review it first. Store the estimated epicenter latitude/longitude and the estimated magnitude on the `EarthquakeAlert`. The status transitions (review, false alarm, etc.) are implemented in delopgave 3; here it is enough that the alert is created in the `UNDER_REVIEW` state.

### Link the relevant readings to the alert

For traceability, an admin must later be able to see exactly which three readings triggered an alert. So the alert keeps a relation to the readings that went into its calculation: set each of the three readings' `alert` reference (`@ManyToOne`) to the new alert, matching the alert's `readings` (`@OneToMany`). You store not only the result (epicenter + magnitude) but also the link back to the data behind it.

### Flow in one POST /api/sensor-data

Parse the list → validate each reading → save the readings (always) → if exactly 3 valid readings: try to estimate the epicenter (catch failure) → estimate the magnitude → create `EarthquakeAlert(status = UNDER_REVIEW, epicenter, magnitude)` → link the 3 readings to the alert. If fewer than 3 valid readings, or estimation fails: stop after saving the readings.

Done when: a request with three valid readings produces an `UNDER_REVIEW` alert with an estimated epicenter and magnitude and the three readings linked to it, while requests with fewer than three valid readings — or where estimation fails — still store their readings and create no alert.

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

## Delopgave 4 — Reverse geocoding

Goal: give each alert a human-readable area derived from its epicenter coordinates. After delopgave 2 an alert only has an epicenter as numbers (e.g. latitude 55.638, longitude 11.142), which means little to a user or admin. Reverse geocoding turns coordinates into a place name — a city, region, or country. (Ordinary geocoding goes the other way: from an address to coordinates.)

The system must be able to:
- determine a geographic area for an alert from the epicenter's coordinates
- use an external API to look the area up
- store the result on the alert (the `area` field that was empty until now)
- allow the external provider to be swapped for another
### Look up the area

When an alert's epicenter has been calculated, call an external reverse-geocoding API with the epicenter's latitude and longitude and read a place name from the response. The suggested provider is OpenStreetMap's Nominatim:

```
GET https://nominatim.openstreetmap.org/reverse?format=json&lat=55.6761&lon=12.5683
```

The response is JSON; pull out a usable area string — for example `display_name`, or specific fields under `address` (city, region, country) — and store it as the alert's `area`.

### Make it swappable

As with the epicenter and magnitude calculations in delopgave 2, the provider must be replaceable. Put the lookup behind a `GeocodingService` interface with a Nominatim implementation, and depend on the interface, not on OpenStreetMap directly. Switching to a different provider later then means writing a new implementation without touching the alert logic.

### Where it fits in the flow

Delopgave 2 creates the alert with an epicenter and magnitude. Delopgave 4 extends alert creation so the system also calls `GeocodingService` with the epicenter coordinates and sets `area` on the alert before it is saved.

### Practical notes

- Nominatim has a usage policy: at most one request per second and an identifying User-Agent/Referer header on the request.
- The call can fail or be slow. Handle it gracefully — for example leave `area` empty if the lookup fails rather than letting the whole alert creation fail.
  Done when: a newly created alert has its `area` populated from the epicenter coordinates via the `GeocodingService` interface, and the provider can be swapped without changing the alert-creation code.
## Frontend

Keep the frontend minimal: **two HTML pages**, one per role. This matches the delopgave 5 roles one-to-one, so features are split by which page they live on instead of hiding/showing elements with role checks in JavaScript — less code overall. A single page is possible but would need more JS to toggle admin controls by role, so two pages is the simpler path.

- `index.html` — USER page
- `admin.html` — ADMIN page

Feature placement:

| Feature | Page | Role | Why |
|---|---|---|---|
| View active alerts | `index.html` | USER | USER may see active alerts |
| Submit a citizen report with intensity | `index.html` | USER | USER may submit one report per alert |
| Show report count per alert | `index.html` | USER | Natural number shown next to each active alert |
| View all alerts | `admin.html` | ADMIN | Includes UNDER_REVIEW / FALSE_ALARM that USER does not see |
| Change status (ACTIVE / FALSE_ALARM / NOT_ACTIVE) | `admin.html` | ADMIN | Only admin changes status (enforce transition rules) |
| View reports for an alert | `admin.html` | ADMIN | Detailed list — admin info |
| View the sensor readings that led to an alert | `admin.html` | ADMIN | Admin may see the raw readings behind an alert |

Keeping it minimal:

- `index.html`: one list of active alerts. Each item shows epicenter, magnitude, report count, and a small form (intensity input + submit button) that POSTs a citizen report.
- `admin.html`: one list of all alerts. Each item shows the status plus status buttons (render only the transitions allowed for the current status: `UNDER_REVIEW → ACTIVE/FALSE_ALARM`, `ACTIVE → NOT_ACTIVE`), and two expandable sections — "reports" and "sensor readings" — fetched on click.
- The backend enforces the status transition rules (source of truth); the frontend only shows the relevant buttons to avoid confusion, so the rules are not duplicated in JS.

## Testing

Unit-test the central business rules: validation, the 3-valid-readings → alert rule, "estimation fails → readings saved, no alert", and the status transition rules.

## Public endpoint

`POST /api/sensor-data` must stay public so the Docker container can post without logging in. Everything else is role-protected once delopgave 5 is done.

## Running the sensor simulator

```bash
docker compose up -d        # starts sending readings every 15s to localhost:8080
docker compose logs sensors # check simulated readings / connection errors
```

App must run on `localhost:8080`.