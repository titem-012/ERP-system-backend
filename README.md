# ERP System Backend

This is a Spring Boot application for managing an ERP system (products, transactions, users) with MongoDB and JWT-based authentication.

## Prerequisites

- Java 17
- Maven
- MongoDB (running on localhost:27017 by default)

## Setup and Run

1. Ensure MongoDB is running.
2. Build the project:
   `ash
   mvn clean install -DskipTests
   ``n3. Run the application:
   `ash
   mvn spring-boot:run
   ``n
## Endpoints to Test

### Authentication (Public)
- **POST** /api/auth/login: Login user. Expects `{"username": "admin", "password": "admin123"}`. Returns JWT.
- **POST** /api/auth/register: Register the first admin user.
- **POST** /api/auth/forgot-password: Request a password reset OTP (requires valid email config).
- **POST** /api/auth/reset-password: Reset password using OTP.

### User Management (Requires JWT)
- **POST** /api/auth/change-password: Change the current user's password.
- **GET** /api/users/cashiers: Get all cashiers (Admin only).
- **POST** /api/users/cashier: Create a new cashier (Admin only).
- **DELETE** /api/users/{id}: Delete a user (Admin only).

### Products (Requires JWT)
- **GET** /api/products: Get all products.
- **GET** /api/products/store/{storeId}: Get products for a specific store.
- **POST** /api/products: Create a new product (Admin only).
- **PUT** /api/products/{id}: Update a product (Admin only).
- **DELETE** /api/products/{id}: Delete a product (Admin only).

### Transactions (Requires JWT)
- **GET** /api/transactions: Get all transactions.
- **GET** /api/transactions/store/{storeId}: Get transactions by store.
- **POST** /api/transactions: Create a new transaction (stock-in/stock-out).
- **PUT** /api/transactions/{id}: Update a transaction (Admin only).
- **DELETE** /api/transactions/{id}: Delete a transaction (Admin only).

### Initial Admin Data
On the first startup, a default admin is created if none exists:
- **Username**: dmin`n- **Password**: dmin123`n
You can use this account to obtain a token at /api/auth/login and test the secured endpoints.