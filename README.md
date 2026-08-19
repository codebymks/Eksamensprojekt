## Description
A full-stack REST web application for earthquake monitoring. Simulated seismic sensors send raw measurements to the system, which stores them. When a single request contains exactly three valid measurements, the system estimates an epicenter and a magnitude and creates an earthquake alert. Citizens can view active alerts and submit reports, while administrators review measurements and manage alert statuses.

## Architecture

- **[controller](./src/main/java/org/example/exam/controller)** – REST-endpoints, receives DTOs and returns responses.
- **[service](./src/main/java/org/example/exam/service)** – business logic: validation, alert creation, estimation, and geocoding.
- **[repository](./src/main/java/org/example/exam/repository)** – Spring Data JPA repositories.
- **[model](./src/main/java/org/example/exam/model)** – JPA entities.
- **[dto](./src/main/java/org/example/exam/dto)** – request/response objects, so the API contract stays separate from the database model.
- **[security](./src/main/java/org/example/exam/security)** – Spring Security configuration.
- **[frontend](./src/main/resources/static)** – HTML, CSS and JavaScript.
- **[Test](./src/test/java/org/example/exam)** - JUnit and Mockito.

# Technologies

| Layer | Technology                                                         |
|-----|--------------------------------------------------------------------|
| Language | Java 21                                                            |
| Framework | Spring Boot 4.1.0 (Web MVC, Data JPA, Validation, Spring Security) |
| Persistence | Spring Data JPA / Hibernate, MySQL                                 |
| Boilerplate | Lombok                                                             |
| Test | JUnit, Mockito                                                     |
| Frontend | HTML, CSS, JavaScript                                              |
| Sensor simulator | Docker (`ghcr.io/osman-butt/exam-seismic-simulator`)               |

## Database

The connection to the [database](./src/main/resources/schema.sql) is managed via environment variables:

- DATABASE_URL → jdbc:mysql://localhost:3306/exam
- DATABASE_USERNAME → your username for database
- DATABASE_PASSWORD → your password for database

