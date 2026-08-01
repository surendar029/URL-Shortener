# PROJECT_CONTEXT.md

# URL Shortener Project Context

## Project Goal

Build a production-ready URL Shortener using Spring Boot while learning the concepts instead of relying on AI-generated code.

The AI should act as a mentor and reviewer, not a code generator.

---

# Tech Stack

- Java 17
- Spring Boot 4.x
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL
- Jakarta Validation
- Lombok

Planned:

- Redis
- Docker
- Spring Boot Actuator
- Prometheus
- Grafana
- Swagger/OpenAPI
- JUnit & Mockito

---

# Learning Rules

When helping with this project:

- Do NOT generate the complete project.
- Explain concepts before code.
- Give hints first.
- Reveal only the next step if I'm stuck.
- Review my code instead of replacing it.
- Suggest production-ready improvements.
- Ask interview questions after each milestone.

---

# Architecture

Client
↓
Controller
↓
Service
↓
Repository
↓
PostgreSQL

Future:

Client
↓
Controller
↓
Service
↓
Redis Cache
↓
Repository
↓
PostgreSQL

---

# Package Structure

dev.project.urlshortener

├── controller
├── service
├── repository
├── entity
├── dto
├── exception
├── config
├── util

---

# Database

Table: url_mapping

Columns

- id
- long_url
- short_code
- created_at
- expiry_date
- click_count

---

# Development Roadmap

## Phase 1 - Proof of Concept (No Database)

### Goal

Build a working URL Shortener using in-memory storage.

### Features

- [x] Create Spring Boot project
- [x] Understand project structure
- [x] Create DTOs
- [x] Create Response DTO
- [x] Create URL Service
- [x] Store URLs using ConcurrentHashMap
- [x] Generate random short code
- [x] POST /shorten API
- [x] GET /{code} redirect API
- [x] Test using Postman

### Concepts

- Spring Boot
- REST APIs
- DTO
- Dependency Injection
- ConcurrentHashMap
- ResponseEntity

Deliverable:
A fully working URL shortener without a database.

---

## Phase 2 - Persistence Layer (PostgreSQL)

### Goal

Replace ConcurrentHashMap with PostgreSQL.

### Features

- [x] Configure PostgreSQL
- [x] Create Entity
- [x] Create Repository
- [x] Replace Map with JPA
- [x] Store URLs permanently
- [x] Handle duplicate codes

### Concepts

- JPA
- Hibernate
- Entity
- Repository
- Transactions

Deliverable:
Data persists even after restarting the application.

---

## Phase 3 - Production APIs

### Features

- [x] Validation
- [x] Global Exception Handler
- [x] Logging
- [x] Clean Service Layer
- [x] Custom Exceptions

### Concepts

- Bean Validation
- @ControllerAdvice
- Logging
- Exception Handling

Deliverable:
Production-quality REST APIs.

---

## Phase 4 - Performance

### Features

- [x] Redis Cache
- [x] Cache Redirect API
- [x] Cache Eviction
- [x] Cache TTL

### Concepts

- Redis
- Caching
- Performance Optimization

Deliverable:
Fast redirect responses with reduced database load.

---

## Phase 5 - Production Readiness

### Features

- [x] Docker
- [x] Docker Compose
- [x] Spring Profiles
- [x] Swagger
- [x] Actuator
- [ ] Prometheus (Optional)
- [ ] Grafana (Optional)

### Concepts

- Containerization
- Monitoring
- API Documentation

Deliverable:
Deployable production-ready application.

---

## Phase 6 - Advanced Features

### Features

- [x] Basic Auth & User Registration
- [x] User Authentication (JWT)
- [x] Custom Alias
- [x] URL Expiration
- [ ] QR Code Generation
- [x] Analytics API
- [ ] Rate Limiting
- [ ] Unit Tests
- [ ] Integration Tests

### Concepts

- Spring Security
- JWT
- Testing
- Scalability
- System Design

Deliverable:
Resume-quality backend project.

# API Endpoints

POST /api/v1/urls

Creates a short URL.

---

GET /{shortCode}

Redirects to the original URL.

---

GET /api/v1/urls/{shortCode}

Returns analytics.

---

DELETE /api/v1/urls/{shortCode}

Deletes a URL.

---

# Coding Standards

- Constructor Injection only
- Layered Architecture
- Follow SOLID principles
- Use Records for DTOs
- Proper Exception Handling
- Meaningful Variable Names
- No Field Injection
- No Business Logic in Controller

---

# Current Task

Current Task:
Phase 6 - Advanced Features (URL Expiration, Analytics API, Custom Alias, Authentication).

Completed:

- Phase 1 Proof of Concept (In-memory URL Shortener)
- Phase 2 Persistence Layer (PostgreSQL & JPA Integration)
- Phase 3 Production APIs (Bean Validation, Global Exception Handler, SLF4J Logging, Custom Exceptions)
- Phase 4 Performance (Redis Caching Integration & TTL Expiration)
- Phase 5 Production Readiness (Spring Profiles, Actuator, Swagger/OpenAPI, Dockerfile & Docker Compose)

Next:

- Add URL Expiration feature (6-hour automatic expiry & 410 GONE handling) ✅
- Create Analytics API (GET /api/v1/urls/{shortCode} returning click count & metadata) ✅
- Add Custom Short Code Alias feature (customAlias support & 409 CONFLICT handling) ✅
- Add DELETE API (DELETE /api/v1/urls/{shortCode} & @CacheEvict handling) ✅
- Add Basic Auth & User Registration (Spring Security, BCrypt, UserEntity, CustomUserDetailsService) ✅
- Add JWT Stateless Authentication (Token generation, JwtAuthenticationFilter, Login API) ✅
- Add Rate Limiting & Unit/Integration Tests

---

# AI Instructions

Read this file before answering.

Assume everything listed under "Completed" has already been implemented.

Do not regenerate completed code.

Continue from the Current Task.

Explain concepts before providing code.

Act like a Senior Java Backend Engineer reviewing my implementation.

Ask interview questions whenever a feature is completed.
