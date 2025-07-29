# Invoice Processing System - Docker Setup

This document explains how to run the Invoice Processing System using Docker Compose, including backend, MySQL database, and frontend.

---

## Prerequisites

- Docker installed: [Get Docker](https://docs.docker.com/get-docker/)
- Docker Compose installed (usually comes with Docker Desktop)

---

## Docker Compose Overview

This setup consists of 3 services:

- **backend**: Spring Boot API server
- **mysql**: MySQL 8 database
- **frontend**: React/Vite frontend application

The services are connected on a custom Docker network called `app-network`.

---

## Running the Application

1. Clone the repository and navigate to the project root where the `docker-compose.yml` file is located.

### Step 2: Generate Maven Wrapper (only once)
If the project doesn't already include `mvnw`:

in server directory:
```bash
mvn -N io.takari:maven:wrapper
```
3. In project directory Run the following command to start all services:

```bash
docker-compose up --build
This will:

Build the backend and frontend Docker images

Start the MySQL container with a persistent volume mysql-data to keep your data safe between restarts

Expose ports:

Backend: 8080

MySQL: 3307 (mapped to container's 3306)

Frontend: 80

Persistent Data
The MySQL data is persisted in a Docker volume named mysql-data:

volumes:
  mysql-data:
This means your database will not lose data if you stop or restart containers.

Accessing the Application
Frontend: Open http://localhost in your browser.

Backend API: Accessible on http://localhost:8080

MySQL: Connect via port 3307 (e.g., using MySQL Workbench or CLI).

Stopping the Application
To stop all containers:

bash
docker-compose down
This will stop and remove containers but keep the database data intact.

Cleaning up
If you want to remove all data (including database volume), run:

docker-compose down -v

If ports are busy, change the host ports in docker-compose.yml.

Check logs for errors:
docker-compose logs backend
docker-compose logs mysql
docker-compose logs frontend