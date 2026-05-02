# Taskboard Backend

Spring Boot backend for a personal task board application with JWT authentication and PostgreSQL persistence.

## Setup

### Prerequisites
- Java 21
- Docker Desktop or a local PostgreSQL instance
- Git

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd taskboard
```

### 2. Start PostgreSQL
The project includes a `docker-compose.yml` file for local Postgres:

```bash
docker compose up -d
```

This starts a database with:
- Database: `taskboard`
- Username: `taskboard`
- Password: `taskboard`

### 3. Review local configuration
Default local configuration lives in `src/main/resources/application.properties`.

Important defaults:
- `spring.datasource.url=jdbc:postgresql://localhost:5432/taskboard`
- `spring.datasource.username=taskboard`
- `spring.datasource.password=taskboard`
- `app.jwt.expiration-minutes=60`

If needed, update those values before starting the app.

### 4. Run the application
On macOS/Linux:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API will start on `http://localhost:8080`.

### 5. Verify the app is running
You can test the API with the authentication endpoints:

- `POST /auth/register`
- `POST /auth/login`

After logging in, use the returned JWT to call protected endpoints like:

- `GET /user`
- `GET /boards`

### Notes
- Database tables are created and updated automatically on startup through Hibernate.
- CORS is currently configured for a frontend running on `http://localhost:5173`.

## Running Tests

On macOS/Linux:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```
