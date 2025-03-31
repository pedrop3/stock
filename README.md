# 📦 Stock Management System – README

## 🧾 Project Overview

This project is a complete stock management system built with Java and Spring Boot. It offers precise control over product inventory, tracking stock movements, analyzing turnover, managing ABC classification, and ensuring stock level efficiency.

---

## 🚀 Features

### 🔄 Stock Movement
- Handles product inflows and outflows.
- Keeps a detailed and auditable movement history.

### 📊 Minimum and Maximum Stock Management
- Defines minimum and maximum stock levels per product.
- Sends alerts when stock reaches critical thresholds.

### 🔁 Turnover Analysis
- Analyzes product sales frequency.
- Helps categorize high-turnover and low-turnover items.

### 📈 ABC Curve Classification
- Classifies products into:
  - A: Most valuable
  - B: Intermediate
  - C: Low-value
- Enhances decision-making and focus on Class A items.

### 🛑 Obsolete Product Management
- Identifies and handles slow-moving or outdated products.

---

## 🧠 Architecture & Design

- 📐 **Clean Layered Architecture**: Separation of concerns between controller, service, repository, and domain.
- 🔌 **Strategy Pattern**: Used to process different types of stock movements (IN, OUT).
- 🔍 **OpenAPI (Swagger)**: Auto-generated API documentation for testing and integration.
- ✅ **Testing**:
  - Unit tests for services and strategies.
  - Integration tests for controller and exception handling.
  - HTML reports generated in `build/reports/tests/test/index.html`.

---

## 🧪 Running the Project with Docker

### 🐳 Prerequisites
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### 🛠️ Setup
Use the `docker-compose.yml` file:

### ▶️ Run the App
```bash
docker-compose up --build
```

---

## 🌐 Accessing the Application

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- H2 Console (if enabled): [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## 👨‍💻 Technologies
- Java 21
- Spring Boot
- JPA / Hibernate
- PostgreSQL
- Swagger / OpenAPI
- JUnit / Mockito
- Docker / Docker Compose

---

## 📂 Project Structure
```
src
├── main
│   ├── java/com/learn/stock
│   │   ├── controller
│   │   ├── model
│   │   ├── repository
│   │   ├── service
│   │   ├── strategy
│   │   └── factory
│   └── resources
│       └── db/migration
└── test/java/com/learn/stock
```

---

## ✅ Author
Pedro Santos
