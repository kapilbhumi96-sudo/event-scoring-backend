# Event Scoring Platform — Backend

A Spring Boot REST API powering a real-time competition scoring system, supporting three roles — Organizer, Judge, and Participant — each with distinct permissions, JWT-based authentication, and live leaderboard updates over WebSocket.

## Features

* JWT authentication with role-based access control (Organizer / Judge / Participant)
* Organizers create events and categories (Solo, Duet, Group, etc.)
* Participants register for specific categories
* Judges submit scores per participant, per criteria, per round
* Scoring algorithm drops the highest and lowest judge score (when 3+ judges), averages the rest, with a tiebreaker rule based on highest single-round score
* Live leaderboard broadcast via WebSocket (STOMP) — updates instantly across all connected clients when a score is submitted, no polling or refresh required

## Tech Stack

* Java 17, Spring Boot 4
* Spring Security + JWT (jjwt)
* Spring Data JPA + Hibernate
* MySQL
* Spring WebSocket (STOMP over SockJS)
* Maven

## Running Locally

### Prerequisites

* Java 17 (JDK)
* MySQL Server running locally
* Maven (or use the included `mvnw` wrapper)

### Setup

1. Clone this repository

2. Create a MySQL database:

```sql
CREATE DATABASE event_scoring_db;
```

3. Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/event_scoring_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

4. Run the application:

```bash
./mvnw spring-boot:run
```

Or run `EventScoringBackendApplication.java` directly from your IDE.

The API will be available at `http://localhost:8080`. Tables are created automatically via Hibernate on first run.

## Key Endpoints

| Method | Endpoint                            | Description                                        |
| ------ | ----------------------------------- | -------------------------------------------------- |
| POST   | `/api/auth/register`                | Create a new account (Organizer/Judge/Participant) |
| POST   | `/api/auth/login`                   | Log in, returns a JWT token                        |
| GET    | `/api/auth/me`                      | Get the currently logged-in user's details         |
| POST   | `/api/events`                       | Create a new event (Organizer)                     |
| GET    | `/api/events`                       | List all events                                    |
| GET    | `/api/events/{id}/categories`       | List categories under an event                     |
| POST   | `/api/events/{id}/categories`       | Add a category to an event (Organizer)             |
| POST   | `/api/categories/{id}/register`     | Register the logged-in user as a participant       |
| GET    | `/api/categories/{id}/participants` | List participants in a category                    |
| POST   | `/api/participants/{id}/scores`     | Submit a score for a participant (Judge only)      |
| GET    | `/api/categories/{id}/leaderboard`  | Get the current ranked leaderboard for a category  |

Protected routes require an `Authorization: Bearer <token>` header.

## Live Leaderboard (WebSocket)

Clients connect to `ws://localhost:8080/ws` and subscribe to `/topic/leaderboard/{categoryId}` to receive live-ranked leaderboard updates the instant a judge submits a score.

## Frontend

The companion React frontend for this project is here:

https://github.com/kapilbhumi96-sudo/event-scoring-frontend
