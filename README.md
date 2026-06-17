# Eksamensprojekt

## Beskrivelse
Dette projekt er en webapplikation udviklet til et eksamensprojekt. Applikationen har til formål at [indsæt formål].

## Arkitektur

- [Model](./src/main/java/org/example/exam/model)
- [Repository](./src/main/java/org/example/exam/repository)
- [Service](./src/main/java/org/example/exam/service)
- [Controller](./src/main/java/org/example/exam/controller)
- [Frontend](./src/main/resources/static)
- [Test](./src/test)

## Teknologier

- Java 25
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Hibernate
- H2 database / MySQL
- Lombok
- HTML/CSS
- JavaScript

## Database

Forbindelsen til [databasen](./src/main/resources/data.sql) styres via miljøvariabler:

- URL → JDBC-forbindelsesstreng
- USERNAME → brugernavn til database
- PASSWORD → adgangskode til database
