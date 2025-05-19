#  Banking Platform Microservices

This project is a modular microservices-based banking platform built with **Spring Boot**, **Spring Cloud (Eureka + Feign)**, **PostgreSQL**, and **Docker**. It demonstrates real-world concepts like service-to-service communication, entity ownership validation, and strict domain constraints, while following clean architecture principles.

---

## Microservices Overview

| Service            | Port  | Description                                |
|--------------------|-------|--------------------------------------------|
| `eureka-server`    | 8761  | Service registry for service discovery      |
| `customer-service` | 8081  | Manages customer data                       |
| `account-service`  | 8082  | Manages bank account data                   |
| `card-service`     | 8083  | Manages card details (physical/virtual)     |

All services are registered with **Eureka** and communicate via **OpenFeign**.

---

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Cloud (Eureka Discovery)
- Spring Data JPA
- OpenFeign (HTTP Clients)
- PostgreSQL 15
- Docker & Docker Compose
- JUnit 5 for testing
- Flyway for DB migrations
- Maven for build and dependency management

---

## Getting Started

###  Step 1: Start PostgreSQL using Docker

```bash
docker rm -f banking-microservice

docker run --name banking-microservice \
  -e POSTGRES_USER=njiri \
  -e POSTGRES_PASSWORD=njiri123# \
  -e POSTGRES_DB=customer_service_db \
  -p 5432:5432 \
  -d postgres:15.0

Then connect to it:

docker exec -it banking-microservice psql -U njiri -d customer_service_db
Inside PostgreSQL shell:

CREATE DATABASE account_service_db;
CREATE DATABASE card_service_db;

Step 2: Project Structure

banking-platform-microservices/
│
├── eureka-server/         # Service registry
├── customer-service/      # Handles customer CRUD
├── account-service/       # Handles account CRUD
├── card-service/          # Handles card CRUD
└── pom.xml                # Parent Maven config

Step 3: Build and Run the Services
./mvnw clean install

Then start each service in this order:

cd eureka-server
./mvnw spring-boot:run

cd ../customer-service
./mvnw spring-boot:run

cd ../account-service
./mvnw spring-boot:run

cd ../card-service
./mvnw spring-boot:run

Access Eureka Dashboard at:
http://localhost:8761

Service Communication
 Example Inter-Service Calls:
account-service calls customer-service to verify the customer before account creation.

card-service calls account-service to ensure the account exists before card creation.

account-service can retrieve card info by alias by calling card-service.

Search & Filters
Customer Service:

Full-text name search

Date range filters

Account Service:

Search by IBAN, BIC SWIFT, card alias

Card Service:

Filter by card alias, type (virtual/physical), and masked PAN

Safe Delete Logic
Implemented in service layers:

Cannot delete a customer with linked accounts

Cannot delete an account with linked cards

Cards can be deleted independently

Testing Strategy
All services include unit tests using JUnit 5

Covers service, controller, and repository layers

Validates happy paths and edge cases

Ensures exception handling and fallback logic work correctly

Run tests with

./mvnw test

API Sample Endpoints
customer-service (:8081)
GET /api/customers/{id}

POST /api/customers

GET /api/customers?name=...&start=...&end=...

PUT /api/customers/{id}

DELETE /api/customers/{id}

account-service (:8082)
POST /api/accounts

GET /api/accounts?iban=...&bicSwift=...&cardAlias=...

PUT /api/accounts/{id}

DELETE /api/accounts/{id}

card-service (:8083)
POST /api/cards

GET /api/cards?cardAlias=...&type=...&pan=...

PUT /api/cards/{id}

DELETE /api/cards/{id}

All endpoints return standardized responses using StandardResponse<T> DTO.

 Notes
Each microservice uses its own PostgreSQL schema

Flyway handles DB migrations per service

Exception handling is centralized using @ControllerAdvice

OpenFeign and Eureka simplify internal communication

Docker ensures a consistent and testable database setup

Author
Built Njiri Muraguri
Focused on clean architecture, test-driven development, and real-world problem solving.

