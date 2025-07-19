# Spring Boot JPA Examples with Kotlin

This project demonstrates comprehensive usage of Spring Boot JPA with Kotlin, featuring:

- **4 Entity Relationships**: One-to-One, One-to-Many, Many-to-One, Many-to-Many
- **JPQL Queries**: Custom queries using JP Query Language
- **JPA Criteria API**: Type-safe, programmatic queries
- **MySQL 8 Database**: Container-based setup with Docker Compose
- **Flyway Migrations**: Database schema and data management

## Entity Relationships

### 1. One-to-One: User ↔ UserProfile
- Each user has exactly one profile
- `User.profile` → `UserProfile`
- `UserProfile.user` → `User`

### 2. One-to-Many: User → Orders
- Each user can have multiple orders
- `User.orders` → `List<Order>`

### 3. Many-to-One: Order → User
- Each order belongs to one user
- `Order.user` → `User`

### 4. Many-to-Many: Order ↔ Products
- Each order can contain multiple products
- Each product can be in multiple orders
- `Order.products` → `List<Product>`
- `Product.orders` → `List<Order>`

## Quick Start

### 1. Start MySQL Database
```bash
docker-compose up -d
```

### 2. Run the Application
```bash
./mvnw spring-boot:run
```

### 3. Test the API
The application will start on `http://localhost:8080`

## API Endpoints

### JPQL Examples

#### User Operations
- `GET /api/jpa-examples/users/by-email?email=john.doe@example.com`
- `GET /api/jpa-examples/users/by-name?name=John`
- `GET /api/jpa-examples/users/1/with-profile`
- `GET /api/jpa-examples/users/1/with-orders`
- `GET /api/jpa-examples/users/with-orders-above?amount=500`

#### Order Operations
- `GET /api/jpa-examples/orders/by-user/1`
- `GET /api/jpa-examples/orders/by-status?status=DELIVERED`
- `GET /api/jpa-examples/orders/1/with-products`
- `GET /api/jpa-examples/orders/containing-product/1`
- `GET /api/jpa-examples/orders/total-amount-by-user/1`

#### Product Operations
- `GET /api/jpa-examples/products/by-name?name=Laptop`
- `GET /api/jpa-examples/products/by-price-range?minPrice=100&maxPrice=500`
- `GET /api/jpa-examples/products/out-of-stock`
- `GET /api/jpa-examples/products/popular?minOrders=2`
- `GET /api/jpa-examples/products/average-price`

### JPA Criteria API Examples

#### Advanced User Search
```
GET /api/jpa-examples/users/search?name=John&hasProfile=true&createdAfter=2024-01-01T00:00:00
```

#### User Statistics
- `GET /api/jpa-examples/users/with-order-statistics`
- `GET /api/jpa-examples/users/with-orders-in-status?status=PENDING`

#### Advanced Product Search
```
GET /api/jpa-examples/products/search?name=Laptop&minPrice=1000&maxPrice=2000&minStock=10&orderByPopularity=true
```

#### Product Analytics
- `GET /api/jpa-examples/products/ordered-by-user/1`
- `GET /api/jpa-examples/products/sales-report`
- `GET /api/jpa-examples/products/needing-restock?threshold=20`

#### Advanced Order Search
```
GET /api/jpa-examples/orders/search?userId=1&status=DELIVERED&minAmount=100&maxAmount=1000&startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

#### Order Analytics
- `GET /api/jpa-examples/orders/with-multiple-products?minProductCount=3`
- `GET /api/jpa-examples/orders/daily-summary?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
- `GET /api/jpa-examples/orders/status-counts`

### Relationship Demo
- `GET /api/jpa-examples/relationships/demo`

## Data Operations (CRUD) Examples

### User Operations
```bash
# Create a new user
POST /api/data/users
Content-Type: application/json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}

# Get user by ID
GET /api/data/users/1

# Get all users
GET /api/data/users

# Update user
PUT /api/data/users/1
Content-Type: application/json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}

# Delete user
DELETE /api/data/users/1
```

### User Profile Operations
```bash
# Create user profile (One-to-One relationship)
POST /api/data/users/1/profile
Content-Type: application/json
{
  "phoneNumber": "+1-555-0123",
  "birthDate": "1990-01-15",
  "bio": "Software developer",
  "profilePictureUrl": "https://example.com/avatar.jpg"
}

# Update user profile
PUT /api/data/users/1/profile
Content-Type: application/json
{
  "phoneNumber": "+1-555-0124",
  "bio": "Senior software developer"
}
```

### Product Operations
```bash
# Create a new product
POST /api/data/products
Content-Type: application/json
{
  "name": "Laptop Pro",
  "description": "High-performance laptop",
  "price": 1299.99,
  "stockQuantity": 50
}

# Create multiple products
POST /api/data/products/bulk
Content-Type: application/json
[
  {
    "name": "Mouse",
    "description": "Wireless mouse",
    "price": 49.99,
    "stockQuantity": 100
  },
  {
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 129.99,
    "stockQuantity": 75
  }
]

# Get product by ID
GET /api/data/products/1

# Get all products
GET /api/data/products

# Update product
PUT /api/data/products/1
Content-Type: application/json
{
  "name": "Laptop Pro Updated",
  "price": 1199.99,
  "stockQuantity": 45
}

# Update product stock
PUT /api/data/products/1/stock?stockQuantity=30

# Increase product stock
POST /api/data/products/1/stock/increase?quantity=10

# Decrease product stock
POST /api/data/products/1/stock/decrease?quantity=5

# Delete product
DELETE /api/data/products/1
```

### Order Operations
```bash
# Create a new order (Many-to-One with User, Many-to-Many with Products)
POST /api/data/orders
Content-Type: application/json
{
  "userId": 1,
  "productIds": [1, 2],
  "totalAmount": 1349.98,
  "status": "PENDING"
}

# Get order by ID
GET /api/data/orders/1

# Get all orders
GET /api/data/orders

# Get orders by user
GET /api/data/orders/user/1

# Update order
PUT /api/data/orders/1
Content-Type: application/json
{
  "totalAmount": 1299.99,
  "status": "PROCESSING",
  "productIds": [1, 3]
}

# Update order status
PUT /api/data/orders/1/status?status=SHIPPED

# Cancel order
POST /api/data/orders/1/cancel

# Add product to order
POST /api/data/orders/1/products/3

# Remove product from order
DELETE /api/data/orders/1/products/2

# Delete order
DELETE /api/data/orders/1
```

### Sample Data Creation
```bash
# Create complete sample data set
POST /api/data/demo/create-sample-data
```

## Database Schema

### Tables
- `users` - User information
- `user_profiles` - User profile details (One-to-One with users)
- `products` - Product catalog
- `orders` - Order information (Many-to-One with users)
- `order_products` - Junction table (Many-to-Many between orders and products)

### Sample Data
The application includes sample data with:
- 5 users with profiles
- 8 products
- 8 orders with various statuses
- Multiple order-product relationships

## JPA/Hibernate Configuration

### Open-in-View Pattern
- **Disabled**: `spring.jpa.open-in-view: false` for better performance
- Prevents lazy loading issues by ensuring transactions are properly managed

### SQL Logging & Debugging
- **Show SQL**: All SQL statements are logged with formatting
- **Parameter Values**: Actual parameter values are shown in logs
- **SQL Comments**: Generated SQL includes helpful comments
- **Performance Monitoring**: Slow query detection (>1000ms)
- **Transaction Logging**: Transaction boundaries are logged

### SQL Logging Test Endpoints
```bash
# Create operations (INSERT statements)
POST /api/sql-logging-test/create-user?name=John&email=john@example.com
POST /api/sql-logging-test/create-product?name=Laptop&price=1299.99&stockQuantity=10
POST /api/sql-logging-test/create-order?userId=1&productId=1&totalAmount=1299.99

# Read operations (SELECT statements)
GET /api/sql-logging-test/find-user-by-email?email=john@example.com
GET /api/sql-logging-test/find-users-by-name?name=John
GET /api/sql-logging-test/find-products-by-price-range?minPrice=100&maxPrice=2000
GET /api/sql-logging-test/find-orders-by-user?userId=1

# Update operations (UPDATE statements)
PUT /api/sql-logging-test/update-user/1?name=John Updated&email=john.updated@example.com

# Delete operations (DELETE statements)
DELETE /api/sql-logging-test/delete-user/1

# Transaction test
GET /api/sql-logging-test/transaction-test
```

### Example SQL Log Output
```sql
-- User creation
Hibernate: 
    insert 
    into
        users
        (created_at, email, name) 
    values
        (?, ?, ?)
2024-01-20 10:30:45 - binding parameter [1] as [TIMESTAMP] - [2024-01-20T10:30:45.123]
2024-01-20 10:30:45 - binding parameter [2] as [VARCHAR] - [john@example.com]
2024-01-20 10:30:45 - binding parameter [3] as [VARCHAR] - [John Doe]

-- User search with JPQL
Hibernate: 
    select
        u1_0.id,
        u1_0.created_at,
        u1_0.email,
        u1_0.name 
    from
        users u1_0 
    where
        u1_0.email=?
2024-01-20 10:30:45 - binding parameter [1] as [VARCHAR] - [john@example.com]
```

## HikariCP Database Pool Configuration

This application uses HikariCP as the database connection pool with optimized settings for MySQL 8.

### HikariCP Monitoring Endpoints

```bash
# Get current pool statistics
GET /api/metrics/hikari/pool-stats

# Get HikariCP configuration
GET /api/metrics/hikari/config

# Get pool health status
GET /api/metrics/hikari/health

# Spring Boot Actuator endpoints
GET /actuator/health
GET /actuator/metrics
```

### Pool Configuration
- **Pool Name**: HikariCP-POC
- **Minimum Idle**: 5 connections
- **Maximum Pool Size**: 20 connections
- **Connection Timeout**: 30 seconds
- **Idle Timeout**: 5 minutes
- **Max Lifetime**: 30 minutes
- **Leak Detection**: 60 seconds

### MySQL Performance Optimizations
- **Prepared Statement Caching**: Enabled
- **Server-side Prepared Statements**: Enabled
- **Batch Rewriting**: Enabled
- **Connection Init SQL**: UTF8MB4 encoding

### Health Check Example Response
```json
{
  "status": "HEALTHY",
  "poolUtilization": "25%",
  "connections": {
    "active": 2,
    "idle": 3,
    "total": 5,
    "awaiting": 0,
    "max": 20
  },
  "alerts": []
}
```

## Technologies Used

- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **Kotlin 2.1.21**
- **MySQL 8.0**
- **HikariCP** for database connection pooling
- **Spring Boot Actuator** for monitoring
- **Flyway** for database migrations
- **Docker Compose** for containerization

## Key Features Demonstrated

### JPQL Features
- Basic queries with parameters
- JOIN operations
- Subqueries
- Aggregate functions (COUNT, SUM, AVG)
- Custom projections

### JPA Criteria API Features
- Type-safe queries
- Dynamic query building
- Complex predicates
- Subqueries and EXISTS
- Grouping and aggregation
- Multi-table joins

### JPA Relationship Features
- Lazy/Eager loading
- Cascade operations
- Fetch joins
- Bidirectional relationships
- Join table configuration

## Running Tests

```bash
./mvnw test
```

## Database Access

MySQL is accessible at:
- **Host**: localhost
- **Port**: 3306
- **Database**: poc_jpa_db
- **Username**: root
- **Password**: password

## Stopping the Application

```bash
# Stop the application
Ctrl+C

# Stop MySQL container
docker-compose down
```