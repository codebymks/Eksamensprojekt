## Beskrivelse
En fuld-stack REST-webapplikation til overvågning af jordskælv. Simulerede seismiske sensorer sender rå målinger til systemet, som gemmer dem. Når en enkelt forespørgsel indeholder præcis tre gyldige målinger, estimerer et epicenter og en styrke og opretter en jordskælvsalarm. Borgere kan se aktive alarmer og indsende rapporter, mens administratorer gennemgår målinger og styrer alarmernes status.

## Arkitektur

- **[controller](./src/main/java/org/example/exam/controller)** – REST-endpoints, modtager DTO'er og returnerer svar.
- **[service](./src/main/java/org/example/exam/service)** – forretningslogik: validering, alarmoprettelse, estimering og geocoding.
- **[repository](./src/main/java/org/example/exam/repository)** – Spring Data JPA-repositories.
- **[model](./src/main/java/org/example/exam/model)** – JPA-entiteter.
- **[dto](./src/main/java/org/example/exam/dto)** – request/response-objekter, så API-kontrakten holdes adskilt fra databasemodellen.
- **[security](./src/main/java/org/example/exam/security)** – Spring Security-konfiguration.
- **[frontend](./src/main/resources/static)** – statiske sider og JavaScript.
- **[Test](./src/test/java/org/example/exam)** - JUnit og Mockito.

# Teknologier

| Lag | Teknologi |
|-----|-----------|
| Sprog | Java 21 |
| Framework | Spring Boot 4.1.0 (Web MVC, Data JPA, Validation, Security) |
| Persistens | Spring Data JPA / Hibernate, MySQL |
| Boilerplate | Lombok |
| Test | JUnit 5, Mockito |
| Frontend | HTML, CSS, JavaScript |
| Sensor-simulator | Docker (`ghcr.io/osman-butt/exam-seismic-simulator`) |

## Database

Forbindelsen til [databasen](./src/main/resources/schema.sql) styres via miljøvariabler:

- URL → jdbc:mysql://localhost:3306/exam
- USERNAME → brugernavn til database
- PASSWORD → adgangskode til database

