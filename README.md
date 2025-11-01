# 🚀 RAG Chat Service — Backend (Spring Boot)

> Backend service for the **RAG Chat Service**, a containerized Spring Boot application that provides APIs for storing and managing chat messages.  
> This README covers project overview, setup, configuration, APIs, Docker usage, and troubleshooting.

---

## 🧭 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Configuration & Environment Variables](#configuration--environment-variables)
- [Quick Start — Development](#quick-start--development)
- [Quick Start — Production-like](#quick-start--production-like)
- [Build & Run Locally (Without Docker)](#build--run-locally-without-docker)
- [API Endpoints](#api-endpoints)
- [Authentication / API Key](#authentication--api-key)
- [Database & DB Tools](#database--db-tools)
- [Common Issues & Troubleshooting](#common-issues--troubleshooting)
- [Contributing](#contributing)
- [License & Contact](#license--contact)

---

## 🧩 Project Overview

The **RAG Chat Service** is a backend component designed for storing and retrieving chat messages.  
It is built with **Spring Boot**, uses **MySQL** as the database, and is fully **Dockerized** for local and production deployments.

This service can serve as the backend for a Retrieval-Augmented Generation (RAG)-based chatbot or any chat-driven microservice.

---

## ✨ Features

- REST APIs for chat operations (CRUD)
- Health check endpoint (`/api/v1/health`)
- API-key-based authentication
- Multiple API Keys Support
- Global exception handling
- Dockerized setup for local and production environments
- Swagger/OpenAPI documentation
- Configurable via `.env` files
- Resilience4j rate-limiting (for selected endpoints)
- Easy database access via Adminer (optional in Docker setup)

---

## 🏗️ Architecture & Tech Stack

| Layer | Technology |
|-------|-------------|
| Backend Framework | Spring Boot |
| Language | Java 17+ |
| Build Tool | Maven |
| Database | MySQL 8 |
| Containerization | Docker & Docker Compose |
| API Docs | OpenAPI / Swagger |
| Security | API Key-based |
| Logging | SLF4J + Lombok |
| Rate Limiting | Resilience4j |
| Global Exception Handling | Spring Boot |

---

## 📂 Project Structure

```text
rag-chat-service/
├── src/
│   ├── main/java/com/firefist/rag_chat_service/
│   │   ├── config/            # LLM and Swagger Config
│   │   ├── controller/        # REST controllers
│   │   ├── dto/               # DTO to handle request and response
│   │   ├── security/          # API keys Security
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Spring Data JPA repositories
│   │   ├── model/             # Entity models
│   │   └── exception/         # Custom/global exception handling
│   └── resources/
│       ├── application.yml
│       ├── static/
│       └── templates/
├── Dockerfile
├── docker-compose.yml
├── .env.dev
├── .env.prod
└── README.md
```

---

## ⚙️ Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git**

---

## 🔐 Configuration & Environment Variables

Use environment files for separating development and production setups.  
Create `.env.dev` and `.env.prod` as per your environment.

### Example `.env.dev` file

```env.dev
# App
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Single legacy key (optional)
API_KEY=
# Prefer multiple keys (comma-separated, no spaces)
API_KEYS=Strong@Dev1,Strong@Dev2

# MySQL (container-facing port should remain 3306)
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_PORT_HOST=3306
MYSQL_DATABASE=rag_chat_dev

MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_USER=user
MYSQL_PASSWORD=password

# optional: unique volume name if you must use fixed names
MYSQL_VOLUME_NAME=mysql-data-dev

# Other (optional)
REDIS_HOST=redis
REDIS_PORT=6379

# Adminer host port mapping
ADMINER_PORT_HOST=8081

#LLM
LLM_API_KEY=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
LLM_ENDPOINT=https://api.openai.com/v1/chat/completions
LLM_MODEL=gpt-4o-mini
LLM_ENABLED=false
```

### Example `.env.prod` file

```env
# App
SERVER_PORT=8087
SPRING_PROFILES_ACTIVE=prod
# API Key - required for all non-whitelisted endpoints
API_KEY=StrongKey@Prod

# API Keys (comma-separated). Adding as many keys as we want.
# Example:
API_KEYS=ProdKey@Secure,ProdKey@Backup

# MySQL (container-facing port should remain 3306)
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_PORT_HOST=3308
MYSQL_DATABASE=rag_chat_prod

# optional: unique volume name if you must use fixed names
MYSQL_VOLUME_NAME=mysql-data-prod

MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_USER=user
MYSQL_PASSWORD=password

# Other (optional)
REDIS_HOST=redis
REDIS_PORT=6391

# Adminer host port mapping
ADMINER_PORT_HOST=8082

#LLM
LLM_API_KEY=
LLM_ENDPOINT=https://api.openai.com/v1/chat/completions
LLM_MODEL=gpt-4o-mini
LLM_ENABLED=false
```

> **Note:** The internal MySQL port (`3306`) remains the same inside the container.  
> If running both environments locally, map host ports differently (e.g., `3306` → `3307` for prod).

---

## 🧑‍💻 Quick Start — Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/firefist28/rag-chat-service.git
   cd rag-chat-service
   ```

2. **Start containers**
   ```bash
   docker compose -p rag-chat-dev --env-file .env.dev up -d
   ```

3. **Access services**

| Service | URL |
|----------|-----|
| Backend API | [http://localhost:8080](http://localhost:8080) |
| Health Check | [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health) |
| Swagger UI | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| Adminer | [http://localhost:8081](http://localhost:8081) |

---

## 🧱 Quick Start — Production-like

1. **Update and verify** `.env.prod`
2. **Run the stack**
   ```bash
   docker compose -p rag-chat-prod --env-file .env.prod up -d
   ```
3. **Access backend**
   - Example: [http://localhost:8087](http://localhost:8087)
4. **Check logs**
   ```bash
   docker compose logs backend --tail=100
   ```

---

## 🧰 Build & Run Locally (Without Docker)

1. **Build with Maven**
   ```bash
   ./mvnw clean package -DskipTests
   ```
2. **Run the JAR**
   ```bash
   java -jar target/*.jar
   ```
3. **Access locally**
   - [http://localhost:8080](http://localhost:8080)

---

## 📡 API Endpoints

| Method | Endpoint                                | Description              | Auth Required |
|--------|-----------------------------------------|--------------------------|---------------|
| GET    | `/api/v1/health`                        | Check service health     | ❌             |
| POST   | `/api/v1/sessions`                      | Create a session         | ✅             |
| GET    | `/api/v1/sessions/{id}`                 | Get session              | ✅             |
| GET    | `/api/v1/sessions/user/{userId}`        | Get session By User Id   | ✅             |
| DELETE | `/api/v1/sessions/{id}`                 | Soft Delete Session      | ✅             |
| POST   | `/api/v1/sessions/{id}/favorite`        | Set a session favorite   | ✅             |
| POST   | `/api/v1/sessions/{id}/rename`          | Rename session           | ✅             |
| GET    | `/api/v1/sessions/{sessionId}/messages` | Get message from session | ✅             |
| POST   | `/api/v1/sessions/{sessionId}/messages` | Create a new message     | ✅             |
| GET    | `/swagger-ui/**`                        | Swagger documentation    | ❌             |

---

## 🔑 Authentication / API Key

All endpoints (except `/api/v1/health` and Swagger paths) require an API key.

Include it in headers:

```http
X-API-KEY: <your-key>
```

Example configuration in `application.yml`:

```yaml
security:
  apikey:
    enabled: true
    keys: "StrongKey@Dev"
    whitelist: "/api/v1/health,/swagger-ui,/v3/api-docs"
```

---

## 🗄️ Database & DB Tools

- **DB:** MySQL 8  
- **Database name:** `rag_chat`  
- **Host (Docker internal):** `mysql`  
- **Port:** `3306`  
- **Adminer:** [http://localhost:8081](http://localhost:8081)

| Key | Value |
|------|--------|
| Host | mysql |
| Port | 3306 |
| Username | raguser |
| Password | ragpass |
| Database | rag_chat |

---

## 🧩 Common Issues & Troubleshooting

| Issue | Solution |
|--------|-----------|
| Backend shows port 8080 even in prod | Container runs on internal port 8080; map `8087:8080` in Docker Compose |
| MySQL port conflicts | Use `3307:3306` for a second instance |
| DB migration FK error | Drop dependent foreign keys before altering columns |
| Empty logs | Run `docker compose up` without `-d` to view real-time logs |

---

## 🤝 Contributing

1. Fork this repository  
2. Create a feature branch  
   ```bash
   git checkout -b feature/my-feature
   ```
3. Commit and push your changes  
4. Open a Pull Request 🎉

> Avoid committing sensitive data such as `.env` files.

---

### 🧠 LLM Integration (Experimental)
- Added mock **LLM integration layer** to simulate interaction with a language model.
- Controlled via the environment variable:
  ```env
  LLM_ENABLED=true
  LLM_API_KEY=your-paid-llm-api-key-if-you-have-one
  ```
- **Behavior:**
    - If `LLM_ENABLED=false` → LLM service is disabled.
    - If `LLM_ENABLED=true` but no valid `LLM_API_KEY` → mock responses are generated.
- Since no paid LLM service is connected, the current behavior only serves **mock responses**.
- This feature can be enhanced later by plugging in a real LLM provider.

---

## 📜 License & Contact

- **License:** MIT  
- **Author:** [firefist28](https://github.com/firefist28)

For any questions or collaborations, please open an issue or reach out on GitHub.

---

## 🧠 Notes

- Internal MySQL port remains `3306`; host mappings may differ.  
- JPA auto-creates schema if `spring.jpa.hibernate.ddl-auto` is configured.  
- Swagger UI becomes available after service starts.  
- Future integration planned for LLM-based RAG chatbot interface.

---

⭐ **If you found this project helpful, please consider giving it a star!**
