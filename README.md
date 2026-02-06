# 📊 Order Execution & Risk Management System

A production-quality order execution platform built with Java Spring Boot, demonstrating real-world trading system architecture with comprehensive risk management, transaction handling, and audit trails.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-success.svg)]()

## 🎯 Overview

This system simulates a stock trading platform's order execution engine, handling order creation, validation, execution, and comprehensive audit logging. Built with enterprise-grade architecture and best practices, it showcases modern Java backend development skills.

### Key Features

- 🔄 **Real-time Order Execution** - Market and limit orders with instant processing
- 💰 **Account Management** - Multi-account support with balance tracking and validation
- 🛡️ **Risk Management** - Pre-trade validation, balance checks, and account status verification
- 📊 **Audit Trail** - Complete execution history with detailed logging
- 🌐 **Web Dashboard** - Modern UI for order management and monitoring
- 🔌 **RESTful API** - Clean REST endpoints with proper HTTP semantics
- 🧪 **Comprehensive Testing** - Unit and integration tests with 70%+ coverage
- 🐳 **Docker Ready** - Containerized deployment with Docker Compose

## 🏗️ Architecture

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.2 |
| **Data Access** | Spring Data JPA, Hibernate |
| **Database** | H2 (dev), PostgreSQL (prod) |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |
| **Build** | Maven |
| **Containerization** | Docker, Docker Compose |

### Layered Architecture

```
┌─────────────────────────────────────┐
│   Controllers (REST API Layer)      │  ← HTTP Endpoints
├─────────────────────────────────────┤
│   Services (Business Logic)         │  ← Order Execution, Validation
├─────────────────────────────────────┤
│   Repositories (Data Access)        │  ← JPA/Hibernate
├─────────────────────────────────────┤
│   Entities (Domain Model)           │  ← User, Account, Order, Log
└─────────────────────────────────────┘
```

### Domain Model

```
User (1) ──→ (N) Account (1) ──→ (N) Order (1) ──→ (N) ExecutionLog
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional)

### Running Locally

```bash
git clone https://github.com/yourusername/order-execution-system.git
cd order-execution-system

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Points

| Service | URL | Description |
|---------|-----|-------------|
| **Web UI** | http://localhost:8080 | Interactive dashboard |
| **REST API** | http://localhost:8080/api | API endpoints |
| **H2 Console** | http://localhost:8080/h2-console | Database viewer |
| **Health Check** | http://localhost:8080/actuator/health | Application status |

**H2 Console Credentials:**
- JDBC URL: `jdbc:h2:mem:orderdb`
- Username: `sa`
- Password: *(leave empty)*

## 📡 API Documentation

### Orders API

#### Create Order
```http
POST /api/orders
Content-Type: application/json

{
  "accountId": 1,
  "symbol": "AAPL",
  "type": "MARKET",
  "side": "BUY",
  "quantity": 100
}
```

#### Get All Orders
```http
GET /api/orders
```

#### Get Order by ID
```http
GET /api/orders/{id}
```

#### Cancel Order
```http
PUT /api/orders/{id}/cancel
```

### Accounts API

#### Get All Accounts
```http
GET /api/accounts
```

#### Get Account by ID
```http
GET /api/accounts/{id}
```

### Execution Logs API

#### Get Execution Logs
```http
GET /api/execution-logs
```

#### Get Logs by Order
```http
GET /api/execution-logs/order/{orderId}
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Test Coverage
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Repository queries and controller endpoints
- **Coverage**: 70%+ across all layers

### Example Test Scenarios
- ✅ Order creation with valid account
- ✅ Order rejection with insufficient balance
- ✅ Account status validation
- ✅ Transaction rollback on failure
- ✅ Execution log creation

## 🐳 Docker Deployment

### Using Docker Compose

```bash
# Run with H2 (Development)
docker-compose up app-dev

# Run with PostgreSQL (Production)
docker-compose up postgres app-prod
```

### Build Docker Image

```bash
docker build -t order-execution-system:latest .
```

## � Database Schema

### Tables

**users**
- Stores user information and status
- Indexes: email, status

**accounts**
- Trading accounts with balance tracking
- Indexes: user_id, account_number, status

**orders**
- Order details and execution status
- Indexes: account_id, symbol, status, created_at

**execution_logs**
- Audit trail of all executions
- Indexes: order_id, executed_at

## 🎨 Web Dashboard Features

The included web UI provides:

- 📊 **Real-time Dashboard** - View accounts, orders, and execution logs
- 📝 **Order Creation** - Interactive form for placing orders
- 🔄 **Auto-refresh** - Updates every 10 seconds
- 🎨 **Responsive Design** - Works on desktop and mobile
- ✅ **Status Indicators** - Color-coded order and account statuses

## 🔧 Configuration

### Application Profiles

**Development (default)**
```yaml
spring.profiles.active=dev
# Uses H2 in-memory database
# Auto-creates schema
# Initializes test data
```

**Production**
```yaml
spring.profiles.active=prod
# Uses PostgreSQL
# Requires database setup
# No test data initialization
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | postgres |
| `SPRING_PROFILES_ACTIVE` | Active profile | dev |

## � Project Structure

```
order-execution-system/
├── src/
│   ├── main/
│   │   ├── java/com/portfolio/orderexecution/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── mapper/          # Entity-DTO mappers
│   │   │   ├── repository/      # Data access layer
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── static/          # Web UI files
│   │       └── application.yml  # Configuration
│   └── test/                    # Test classes
├── docker-compose.yml           # Docker setup
├── Dockerfile                   # Container image
├── pom.xml                      # Maven dependencies
└── README.md                    # This file
```

## 🎓 Key Design Decisions

### 1. Layered Architecture
Clean separation of concerns with distinct layers for presentation, business logic, and data access.

### 2. DTO Pattern
Separate API contracts from domain models for flexibility and security.

### 3. Transaction Management
ACID compliance for financial operations using Spring's `@Transactional`.

### 4. Exception Handling
Global exception handler providing consistent error responses.

### 5. Database Indexing
Strategic indexes on frequently queried columns for optimal performance.

### 6. Immediate Execution
Orders execute synchronously for simplicity (can be extended to async).

## 🚀 Future Enhancements

- [ ] Spring Security with JWT authentication
- [ ] WebSocket for real-time order updates
- [ ] Advanced order matching engine
- [ ] Position tracking and portfolio management
- [ ] Market data integration
- [ ] Performance metrics and monitoring
- [ ] Database migration with Flyway/Liquibase

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Fizzexual**

- GitHub: [@fizzexual](https://github.com/fizzexual)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)
- Portfolio: [fizzexual.github.io](https://fizzexual.github.io)

## 🙏 Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- Inspired by real-world trading systems
- Created as a portfolio demonstration project

---

⭐ **Star this repository if you find it helpful!**

*This is a demonstration project showcasing backend development skills. It is not intended for production trading.*
