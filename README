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
│   │   ├── controller/        # REST controllers
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
├── .env
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
Create `.env` and `.env.prod` as per your environment.

### Example `.env` file

```env
# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# API Key
API_KEY=StrongKey@Dev

# MySQL
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=rag_chat
MYSQL_USER=raguser
MYSQL_PASSWORD=ragpass
MYSQL_PORT=3306

# Adminer (optional)
ADMINER_PORT=8081
```

### Example `.env.prod` file

```env
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8087

API_KEY=ProdKey@Secure

MYSQL_ROOT_PASSWORD=ProdRoot@123
MYSQL_DATABASE=rag_chat_prod
MYSQL_USER=raguser
MYSQL_PASSWORD=ragpass
MYSQL_PORT=3306
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

2. **Copy and configure the environment file**
   ```bash
   cp .env.example .env
   ```

3. **Start containers**
   ```bash
   docker compose up -d --build
   ```

4. **Access services**

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
   docker compose --env-file .env.prod up -d --build
   ```
3. **Access backend**
   - Example: [http://localhost:8087](http://localhost:8087)
4. **Check logs**
   ```bash
   docker compose logs backend --tail=100
   ```

> **Note:** The backend may log `Tomcat started on port 8080` (container internal port).  
> Host port mapping (`8087:8080`) defines how it's accessed externally.

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

| Method | Endpoint | Description | Auth Required |
|---------|-----------|--------------|----------------|
| GET | `/api/v1/health` | Check service health | ❌ |
| GET | `/api/v1/messages` | Fetch all chat messages | ✅ |
| GET | `/api/v1/messages/{id}` | Fetch a specific message | ✅ |
| POST | `/api/v1/messages` | Create a new message | ✅ |
| PUT | `/api/v1/messages/{id}` | Update a message | ✅ |
| DELETE | `/api/v1/messages/{id}` | Delete a message | ✅ |
| GET | `/swagger-ui/**` | Swagger documentation | ❌ |

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
