# URL Shortener

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red?style=flat-square&logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=flat-square&logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=flat-square&logo=swagger)](https://swagger.io/)

A production-ready URL Shortener REST API built with Java 17 and Spring Boot 3. Features stateless JWT authentication, Redis caching, Bucket4j rate limiting, click analytics, URL expiration, and Docker orchestration.

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security 6, JWT (JJWT 0.12.5), BCrypt |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL 16 |
| Caching | Redis, Spring Cache (`@Cacheable`, `@CacheEvict`) |
| Rate Limiting | Bucket4j 8.10.1 (Token Bucket Algorithm) |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| API Docs | SpringDoc OpenAPI 3 / Swagger UI |
| Monitoring | Spring Boot Actuator |

---

## Features

- JWT stateless authentication with BCrypt password hashing
- User registration and role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- URL shortening with unique 6-character alphanumeric short codes
- Custom alias support for personalized short links
- URL expiration after 6 hours with `410 Gone` response
- Click analytics — tracks click count, creation time, and expiry
- Redis caching with `@Cacheable` on redirects and `@CacheEvict` on deletion
- API rate limiting — 10 requests per minute per IP using Bucket4j Token Bucket
- URL ownership via `@ManyToOne` / `@OneToMany` JPA relationship
- Centralized exception handling via `@RestControllerAdvice`

---

## API Endpoints

| Method | Endpoint | Auth | Request Body | Response |
|---|---|---|---|---|
| `POST` | `/api/v1/auth/register` | None | `{ "username", "email", "password" }` | `201` — "User registered successfully" |
| `POST` | `/api/v1/auth/login` | None | `{ "username", "password" }` | `200` — JWT token string |
| `POST` | `/api/v1/urls` | Bearer | `{ "longUrl", "customAlias?" }` | `201` — `{ "shortUrl", "longUrl" }` |
| `GET` | `/api/v1/urls/{shortCode}` | Bearer | — | `200` — `{ "shortCode", "longUrl", "clickCount", "createdAt", "expiryDate" }` |
| `DELETE` | `/api/v1/urls/{shortCode}` | Bearer | — | `204` — No Content |
| `GET` | `/{shortCode}` | None | — | `302` — Redirect to original URL |
| `GET` | `/swagger-ui.html` | None | — | Interactive API documentation |
| `GET` | `/actuator/health` | None | — | Application health status |

### HTTP Status Codes

| Status | Meaning |
|---|---|
| `201 Created` | Resource created successfully |
| `302 Found` | Redirect to original URL |
| `400 Bad Request` | Validation error |
| `401 Unauthorized` | Missing or invalid JWT token |
| `404 Not Found` | Short code does not exist |
| `409 Conflict` | Custom alias or username already taken |
| `410 Gone` | Short link has expired |
| `429 Too Many Requests` | Rate limit exceeded (10 req/min per IP) |

---