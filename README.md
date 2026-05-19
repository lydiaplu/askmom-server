# AskMom Server

AskMom Server is a Java Spring Boot microservices backend for the AskMom application. It provides user management, maternal profile management, baby profile management, JWT-based authentication, and a gateway entry point for client applications.

## Architecture

```text
Client
  -> API Gateway :9000
      -> User Service :9180
      -> Maternal Service :9181
      -> Baby Service :9182
      -> MySQL
```

## Services

| Service | Path | Port | Responsibility |
| --- | --- | ---: | --- |
| API Gateway | `askmom-api-gateway` | `9000` | Routes client requests to backend services |
| User Service | `askmom-user-service` | `9180` | Users, authentication, and user profiles |
| Maternal Service | `askmom-maternal-service` | `9181` | Maternal profile data |
| Baby Service | `askmom-baby-service` | `9182` | Baby profile data |
| MySQL | Docker service | `3306` inside Docker | Service databases |

## Tech Stack

- Java 17
- Spring Boot
- Spring Cloud Gateway
- Spring Security with JWT
- Spring Data JPA
- MySQL
- Flyway database migrations
- MapStruct
- Lombok
- Docker Compose

## Gateway Routes

The gateway exposes an `/api` prefix and forwards requests to the internal service routes:

```text
/api/auth/**             -> user-service /auth/**
/api/user/**             -> user-service /user/**
/api/user-profile/**     -> user-service /user-profile/**
/api/maternal-profile/** -> maternal-service /maternal-profile/**
/api/baby/**             -> baby-service /baby/**
```

## Prerequisites

- Docker and Docker Compose
- Java 17
- Maven, or the included Maven wrapper scripts inside each service directory

## Run Locally With Docker Compose

Create a local environment file:

```bash
cp .env.example .env
```

Start the full stack:

```bash
docker compose up --build
```

The API Gateway will be available at:

```text
http://localhost:9000
```

Example login endpoint:

```text
POST http://localhost:9000/api/auth/login
```

## Environment Variables

| Variable | Description | Default |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | MySQL root password used by Docker Compose and services | `change-me` |
| `JWT_SECRET` | Base64-encoded JWT signing secret shared by services | Development example value |
| `JWT_EXPIRATION_IN_MILS` | JWT expiration time in milliseconds | `86400000` |

For local development, copy `.env.example` to `.env` and update the values as needed. Do not commit real secrets.

## Databases

Docker Compose initializes the following MySQL databases:

- `user_service_db`
- `maternal_service_db`
- `baby_service_db`

Each service owns its own Flyway migrations under:

```text
askmom-*/src/main/resources/db/migration
```

## Run Tests

Run tests from an individual service directory:

```bash
cd askmom-user-service
./mvnw test
```

Repeat for the other services as needed:

```bash
cd askmom-maternal-service
./mvnw test

cd askmom-baby-service
./mvnw test
```

## Project Structure

```text
.
├── askmom-api-gateway
├── askmom-user-service
├── askmom-maternal-service
├── askmom-baby-service
├── docker/mysql/init
├── compose.yaml
└── .env.example
```

## Notes

- The real `.env` file is ignored by Git.
- JWT validation is handled inside the backend services.
- The Docker Compose setup is intended for local development.
