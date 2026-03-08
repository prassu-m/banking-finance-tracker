# 💰 Banking & Finance Tracker API

A production-grade **REST API** built with **Spring Boot 3** for managing bank accounts, tracking transactions, setting budgets, and analyzing personal finances. Designed with clean architecture, JWT security, and full test coverage.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## ✨ Features

- **🔐 JWT Authentication** — Stateless auth with access & refresh tokens (BCrypt password hashing)
- **🏦 Account Management** — Create and manage multiple accounts (Checking, Savings, Investment, Credit, Loan)
- **💸 Transactions** — Deposits, withdrawals, transfers, payments with full audit trail
- **📊 Budget Tracking** — Set category budgets with configurable alert thresholds
- **📈 Analytics** — Financial summaries, spending breakdowns, and monthly trend reports
- **🗃️ Database Migrations** — Flyway-managed versioned schema migrations
- **📖 Swagger UI** — Auto-generated interactive API documentation
- **🐳 Docker Ready** — Docker + Docker Compose for one-command startup
- **⚡ Caching** — Spring Cache for frequently accessed data
- **🧪 Tests** — Unit + Integration tests with MockMvc

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.12) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Build Tool | Maven |
| API Docs | SpringDoc OpenAPI 3 (Swagger) |
| Boilerplate | Lombok |
| Testing | JUnit 5, Mockito, MockMvc, H2 (in-memory) |
| Container | Docker + Docker Compose |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (recommended)
- PostgreSQL 16 (if running locally)

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repo
git clone https://github.com/yourusername/banking-finance-tracker.git
cd banking-finance-tracker

# Start PostgreSQL + API
docker-compose up -d

# API is running at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Option 2: Run Locally

```bash
# 1. Start a PostgreSQL instance
createdb banking_db

# 2. Configure environment variables (or edit application.yml)
export DATABASE_URL=jdbc:postgresql://localhost:5432/banking_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

# 3. Build and run
mvn spring-boot:run
```

---

## 📋 API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Register new user |
| `POST` | `/api/v1/auth/login` | Login and receive JWT tokens |

### Accounts
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/accounts` | Create an account |
| `GET` | `/api/v1/accounts` | List all user accounts |
| `GET` | `/api/v1/accounts/{id}` | Get account by ID |
| `PUT` | `/api/v1/accounts/{id}` | Update account |
| `DELETE` | `/api/v1/accounts/{id}` | Deactivate account |
| `GET` | `/api/v1/accounts/total-balance` | Get total balance across all accounts |

### Transactions
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/transactions` | Create transaction (deposit/withdrawal/transfer) |
| `GET` | `/api/v1/transactions` | List transactions (paginated, filterable by date) |
| `GET` | `/api/v1/transactions/{id}` | Get transaction by ID |
| `PATCH` | `/api/v1/transactions/{id}/reconcile` | Mark as reconciled |

### Budgets
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/budgets` | Create a budget |
| `GET` | `/api/v1/budgets` | List active budgets (with real-time spending) |
| `GET` | `/api/v1/budgets/{id}` | Get budget details |
| `PUT` | `/api/v1/budgets/{id}` | Update budget |
| `DELETE` | `/api/v1/budgets/{id}` | Delete budget |

### Analytics
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/analytics/summary` | Financial summary with spending breakdown & trends |

---

## 🔑 Authentication

All endpoints (except `/auth/**`) require a `Bearer` token in the `Authorization` header.

```bash
# 1. Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'

# 2. Use the token
curl http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer <your_access_token>"
```

---

## 📊 Example: Full Workflow

```bash
# Register
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Smith","email":"jane@example.com","password":"Pass1234!"}' \
  | jq -r '.accessToken')

# Create a checking account
ACCOUNT_ID=$(curl -s -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Main Checking","type":"CHECKING","currency":"USD"}' \
  | jq -r '.id')

# Deposit money
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"DEPOSIT\",\"category\":\"INCOME\",\"amount\":5000,\"sourceAccountId\":\"$ACCOUNT_ID\"}"

# Set a monthly food budget
curl -X POST http://localhost:8080/api/v1/budgets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Food Budget","category":"FOOD_AND_DINING","limitAmount":500,"period":"MONTHLY","startDate":"2026-03-01","endDate":"2026-03-31","alertThresholdPercent":80}'

# View analytics
curl "http://localhost:8080/api/v1/analytics/summary" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run with coverage report
mvn verify

# Run a specific test class
mvn test -Dtest=TransactionServiceTest
```

---

## 🏗️ Project Structure

```
src/
├── main/java/com/bankingapi/
│   ├── config/             # SecurityConfig, OpenApiConfig
│   ├── controller/         # REST Controllers (Auth, Account, Transaction, Budget, Analytics)
│   ├── dto/
│   │   ├── request/        # Request DTOs with validation
│   │   └── response/       # Response DTOs
│   ├── entity/             # JPA Entities (User, Account, Transaction, Budget)
│   ├── enums/              # AccountType, TransactionType, Category, BudgetPeriod
│   ├── exception/          # Custom exceptions + GlobalExceptionHandler
│   ├── repository/         # Spring Data JPA repositories with custom queries
│   ├── security/           # JWT filter, UserDetailsService, JwtUtil
│   ├── service/            # Business logic (Auth, Account, Transaction, Budget, Analytics)
│   └── util/               # SecurityUtils, AccountNumberGenerator
├── main/resources/
│   ├── application.yml     # App configuration
│   └── db/migration/       # Flyway SQL migrations
└── test/                   # Unit + Integration tests
```

---

## ⚙️ Configuration

Key environment variables:

| Variable | Default | Description |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/banking_db` | PostgreSQL connection URL |
| `DATABASE_USERNAME` | `postgres` | DB username |
| `DATABASE_PASSWORD` | `postgres` | DB password |
| `JWT_SECRET` | (base64 key) | JWT signing secret — **change in production!** |
| `JWT_EXPIRATION` | `86400000` | Access token TTL (ms) — 24 hours |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token TTL (ms) — 7 days |
| `PORT` | `8080` | Server port |

---

## 📖 API Documentation

Once running, visit:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🛡️ Security Notes

- Passwords are hashed with **BCrypt (strength 12)**
- JWTs are signed with **HMAC-SHA256**
- All endpoints are protected; users can only access their own data
- Optimistic locking via `@Version` prevents concurrent update conflicts
- Input validation on all request DTOs

---

## 📄 License

This project is licensed under the MIT License — see [LICENSE](LICENSE) for details.
