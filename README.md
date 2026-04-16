# E-commerce Backend API

Backend API for an e-commerce system built with Java and Spring Boot.

This project focuses on building a robust and secure REST API, applying best practices such as layered architecture, 
domain-driven design principles, validation, exception handling, and JWT-based authentication.

## Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (Authentication & Authorization)
- JPA / Hibernate
- H2 Database
- Maven

## Features

- User management (CRUD)
- Address management with domain-level validation and consistency rules
- Authentication and authorization using JWT
- Role-based access control
- Global exception handling with standardized error responses
- Input validation with custom constraints

## Security

- Stateless authentication using JWT
- Custom authentication and authorization handlers
- Role-based access control using Spring Security
- Secure password hashing with BCrypt

## Design Decisions

- Domain-driven design (DDD) approach for entity modeling
- User acts as the aggregate root, controlling the lifecycle of related entities such as Address
- Validation is enforced at the domain level to ensure consistency

## Running the application

1. Clone the repository:

```bash
git clone https://github.com/bruno-moura-dev/ecommerce-api.git
```

2. Set environment variables:

```env
ADMIN_USERNAME=admin@test.com
ADMIN_PASSWORD=123456
ADMIN_ROLE=ADMIN
JWT_SECRET=ecommerce-api-secret-key-2026
JWT_EXPIRATION=3600000

The admin user is created on application startup using the provided environment variables.
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

Or run the main class from your IDE:

```java
EcommerceApiApplication
```

4. Access API documentation (Swagger UI):

http://localhost:8080/swagger-ui.html


## API Documentation

Swagger UI available at:

http://localhost:8080/swagger-ui.html

## Notes

This project was developed as part of my backend learning journey, focusing on building production-like APIs using 
clean architecture, domain-driven design principles, and industry best practices.
