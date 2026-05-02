# AskMom Microservices Local Setup

This setup keeps the three existing business services unchanged and adds a simple API Gateway plus Docker Compose orchestration.

## Architecture

```text
Client
  -> askmom-api-gateway :9000
      -> askmom-user-service :9180
      -> askmom-maternal-service :9181
      -> askmom-baby-service :9182
  -> mysql :3306
```

## Gateway Routes

The gateway adds an `/api` prefix and forwards requests to the existing controller paths:

```text
/api/auth/**             -> user-service /auth/**
/api/user/**             -> user-service /user/**
/api/user-profile/**     -> user-service /user-profile/**
/api/maternal-profile/** -> maternal-service /maternal-profile/**
/api/baby/**             -> baby-service /baby/**
```

JWT validation remains inside the existing services for this first version.

## Run Locally

```bash
cp .env.example .env
docker compose up --build
```

The API Gateway will be available at:

```text
http://localhost:9000
```

Example login URL through the gateway:

```text
POST http://localhost:9000/api/auth/login
```

## Rollback

This directory is not currently a git repository, so rollback is file based.

To remove this microservices layer, delete:

```text
askmom-api-gateway/
compose.yaml
.env
.env.example
MICROSERVICES_SETUP.md
docker/mysql/init/01-create-databases.sql
askmom-user-service/Dockerfile
askmom-user-service/.dockerignore
askmom-maternal-service/Dockerfile
askmom-maternal-service/.dockerignore
askmom-baby-service/Dockerfile
askmom-baby-service/.dockerignore
```

No existing Java business code was changed.
