# E-commerce Backend API

Backend API for an e-commerce system built with Java and Spring Boot.

This project focuses on building a robust and secure REST API, applying best practices such as layered architecture,
domain-driven design (DDD), validation, exception handling, and JWT-based authentication.

---

## 🚧 Project Status

In active development

- unit tests implemented
- authentication integration tests implemented
- preparing cloud deployment

---

## 🔄 API Flow

1. User registers or logs in
2. Receives JWT token
3. Uses token to access protected endpoints
4. Manages resources (User, Address) securely

---

## ⚙️ Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (Authentication & Authorization)
- JPA / Hibernate
- H2 Database (development)
- PostgreSQL
- Dockerized application
- JUnit 5
- Mockito
- MockMvc
- Maven

---

## 🚀 Features

- User management (CRUD)
- Address management with domain-level validation and consistency rules
- Authentication and authorization using JWT
- Role-based access control
- Global exception handling with standardized error responses
- Input validation with custom constraints
- Unit tests
- Integration tests for authentication flow
- Dockerized deployment
- Environment profiles (dev and prod)

---

## 🔐 Security

- Stateless authentication using JWT
- Custom authentication and authorization handlers
- Role-based access control using Spring Security
- Secure password hashing with BCrypt

---

## 🧠 Design Decisions

- Domain-driven design (DDD) approach for entity modeling
- User acts as the aggregate root, controlling the lifecycle of related entities such as Address
- Validation is enforced at the domain level to ensure consistency
- Address was modeled with behavior similar to a Value Object to encapsulate address consistency rules, while still being implemented as an entity to support updates without requiring removal and recreation
- Environment-specific configuration was separated using Spring Profiles (`dev` and `prod`)
- Authentication flow was designed using stateless JWT-based security

---

## 📌 Example Request

POST /auth/login

```json
{
  "email": "admin@test.com",
  "password": "Admin@123"
}
```

---

## 📌 Example Response

```json
{
  "token": "jwt_token_here"
}
```

---

## ▶️ Running the application

### 1. Clone the repository

```bash
git clone https://github.com/bruno-moura-dev/ecommerce-api.git
cd ecommerce-api
```

---

### 2. Environment profiles

The application supports multiple environments through Spring Profiles:

- `dev` → H2 in-memory database
- `prod` → PostgreSQL database

By default, the application runs using the `dev` profile.

---

### 3. Set environment variables

```env
ADMIN_USERNAME=admin@test.com
ADMIN_PASSWORD=Admin@123
ADMIN_ROLE=ADMIN

JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=3600000

DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
```

The admin user is created on application startup using the provided environment variables.

---

### 4. Run locally

```bash
./mvnw spring-boot:run
```

Or run the main class from your IDE:

```md
EcommerceApiApplication
```
---

### 5. Run with Docker

Build the image:

```bash
docker build -t ecommerce-api .
```

Run the container:

```bash
docker run -p 8080:8080 ecommerce-api
```

---

## 📚 API Documentation

Swagger UI available at:

http://localhost:8080/swagger-ui.html

---

## 📸 API Preview

![Swagger UI](docs/swagger.png)

---

## 🔮 Future Improvements

- Implementation of additional e-commerce domains (Product, Order, Cart, etc.)
- Expanded integration test coverage
- Database migrations with Flyway
- CI/CD pipeline
- Performance optimizations
- Observability improvements

---

## 📝 Notes

This project was developed as part of my backend learning journey, focusing on building production-like APIs using
clean architecture, domain-driven design principles, and industry best practices.

---

## 🚀 Final Notes

This project simulates a production-oriented backend application, focusing on clean architecture, security, testing, containerization, and maintainability.