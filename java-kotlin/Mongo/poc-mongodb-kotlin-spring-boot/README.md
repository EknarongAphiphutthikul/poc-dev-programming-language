# POC MongoDB Kotlin Spring Boot

A proof-of-concept banking application built with Kotlin, Spring Boot, and MongoDB, featuring comprehensive account and customer management functionality.

## 🏗️ Architecture

This application follows a layered architecture pattern:
- **Entity Layer**: MongoDB document models
- **Repository Layer**: Spring Data MongoDB repositories
- **Service Layer**: Business logic implementation
- **Controller Layer**: RESTful API endpoints
- **DTO Layer**: Data transfer objects for API communication

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Docker & Docker Compose

### 1. Start MongoDB
```bash
# Start MongoDB container (first time)
./docker-mongo-up.sh

# Or start existing container
./docker-mongo-start.sh
```

### 2. Run the Application
```bash
# Using Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/poc-mongodb-kotlin-spring-boot-0.0.1-SNAPSHOT.jar
```

### 3. Verify Installation
- Application: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- MongoDB: localhost:27030

## 📋 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Available Endpoints

#### Customer Types
- `GET /customer-types` - List all customer types (paginated)
- `GET /customer-types/{id}` - Get customer type by ID
- `POST /customer-types` - Create new customer type
- `PUT /customer-types/{id}` - Update customer type
- `DELETE /customer-types/{id}` - Delete customer type
- `GET /customer-types/search?name={name}` - Search by name

#### Customers
- `GET /customers` - List all customers (paginated)
- `GET /customers/{id}` - Get customer by ID
- `POST /customers` - Create new customer
- `PUT /customers/{id}` - Update customer
- `DELETE /customers/{id}` - Delete customer
- `GET /customers/search?cifId={cifId}&customerTypeId={typeId}` - Search customers

#### Account Statuses
- `GET /account-statuses` - List all account statuses (paginated)
- `GET /account-statuses/{id}` - Get account status by ID
- `POST /account-statuses` - Create new account status
- `PUT /account-statuses/{id}` - Update account status
- `DELETE /account-statuses/{id}` - Delete account status

#### Account Categories
- `GET /account-categories` - List all account categories (paginated)
- `GET /account-categories/{id}` - Get account category by ID
- `POST /account-categories` - Create new account category
- `PUT /account-categories/{id}` - Update account category
- `DELETE /account-categories/{id}` - Delete account category

#### Accounts
- `GET /accounts` - List all accounts (paginated)
- `GET /accounts/{id}` - Get account by ID
- `POST /accounts` - Create new account
- `PUT /accounts/{id}` - Update account
- `DELETE /accounts/{id}` - Delete account
- `GET /accounts/search?customerId={id}&statusId={id}&productCode={code}` - Search accounts
- `GET /accounts/customer/{customerId}` - Get accounts by customer

#### Account Status History
- `GET /account-status-histories` - List all status histories (paginated)
- `GET /account-status-histories/{id}` - Get status history by ID
- `POST /account-status-histories` - Create new status history
- `PUT /account-status-histories/{id}` - Update status history
- `DELETE /account-status-histories/{id}` - Delete status history
- `GET /account-status-histories/account/{accountId}` - Get history by account

#### Account Authorities
- `GET /account-authorities` - List all account authorities (paginated)
- `GET /account-authorities/{id}` - Get account authority by ID
- `POST /account-authorities` - Create new account authority
- `PUT /account-authorities/{id}` - Update account authority
- `DELETE /account-authorities/{id}` - Delete account authority
- `GET /account-authorities/customer/{customerId}` - Get authorities by customer
- `GET /account-authorities/active` - Get currently active authorities

### Pagination Parameters
All list endpoints support pagination:
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field and direction (e.g., `name,asc`)

Example:
```
GET /api/v1/customers?page=0&size=10&sort=createdAt,desc
```

## 🗄️ Database Schema

### Collections

#### customer_types
```json
{
  "_id": 1,
  "name": "PERSONAL",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": null
}
```

#### customers
```json
{
  "_id": "uuid",
  "cifId": "CIF123456",
  "refKey": "REF789",
  "customerTypeId": 1,
  "createdAt": "2024-01-01T10:00:00",
  "createdBy": "system",
  "updatedAt": null,
  "updatedBy": null
}
```

#### accounts
```json
{
  "_id": "uuid",
  "customerId": "customer-uuid",
  "productId": "product-uuid",
  "productCode": "SAV001",
  "productCategory": "SAVINGS",
  "accountNumber": "1234567890",
  "parentAccountId": null,
  "accountRefKey": "ACC-REF-123",
  "accountCategoryId": 1,
  "statusId": 2,
  "interestType": "SIMPLE",
  "openedDate": "2024-01-01T10:00:00",
  "closureDate": null,
  "attributes": {
    "minBalance": 1000,
    "maxWithdrawal": 50000
  },
  "createdAt": "2024-01-01T10:00:00",
  "createdBy": "system",
  "updatedAt": null,
  "updatedBy": null
}
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories
```bash
# Service tests only
mvn test -Dtest="*ServiceTest"

# Controller tests only
mvn test -Dtest="*ControllerTest"
```

### Test Coverage
The application includes comprehensive unit tests:
- **Service Tests**: Business logic validation with mocked repositories
- **Controller Tests**: REST API endpoint validation with MockMvc
- **Error Scenario Testing**: Exception handling and validation
- **Integration Scenarios**: Complex business workflows

## 🐳 Docker Management

### MongoDB Scripts
```bash
# Start MongoDB (creates container if not exists)
./docker-mongo-up.sh

# Stop and remove MongoDB container
./docker-mongo-down.sh

# Start existing MongoDB container
./docker-mongo-start.sh

# Stop MongoDB container (keeps container)
./docker-mongo-stop.sh
```

### MongoDB Access
```bash
# Connect to MongoDB
docker exec -it poc-mongodb mongosh -u admin -p password --authenticationDatabase admin

# Use application database
use poc_db

# Show collections
show collections
```

## ⚙️ Configuration

### Application Properties
Key configuration in `src/main/resources/application.properties`:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://admin:password@localhost:27030/poc_db?authSource=admin
spring.data.mongodb.database=poc_db

# Health Monitoring
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.health.mongo.enabled=true

# Server Configuration
server.port=8080
```

### Environment Variables
You can override configuration using environment variables:
- `MONGODB_URI` - MongoDB connection string
- `SERVER_PORT` - Application server port
- `MONGODB_DATABASE` - Database name

## 📦 Project Structure

```
src/
├── main/kotlin/th/eknarong/aph/poc/pocmongodbkotlinspringboot/
│   ├── controller/          # REST Controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # MongoDB Entities
│   ├── exception/           # Custom Exceptions
│   ├── repository/          # MongoDB Repositories
│   ├── service/             # Business Logic Services
│   └── PocMongodbKotlinSpringBootApplication.kt
├── main/resources/
│   └── application.properties
└── test/kotlin/th/eknarong/aph/poc/pocmongodbkotlinspringboot/
    ├── controller/          # Controller Tests
    └── service/             # Service Tests
```

## 🔍 Monitoring & Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### MongoDB Health Check
The application includes MongoDB connectivity monitoring through Spring Boot Actuator.

## 🚨 Error Handling

The application provides structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer with id 'invalid-id' not found",
  "path": "/api/v1/customers/invalid-id"
}
```

### Common HTTP Status Codes
- `200 OK` - Successful GET/PUT operations
- `201 Created` - Successful POST operations
- `204 No Content` - Successful DELETE operations
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Unexpected errors

## 🔄 Data Migration

### Initial Data Setup
The application supports seeding initial reference data. You can add initialization scripts in `src/main/resources/` or create a `@PostConstruct` method in a configuration class.

### Sample Data Creation
Use the REST APIs to create sample data:

```bash
# Create customer types
curl -X POST http://localhost:8080/api/v1/customer-types \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "name": "PERSONAL"}'

# Create account statuses
curl -X POST http://localhost:8080/api/v1/account-statuses \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "name": "ACTIVE"}'
```

## 🛠️ Development

### Build Application
```bash
mvn clean compile
```

### Package Application
```bash
mvn clean package
```

### Run in Development Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📝 API Examples

### Create Customer
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "id": "cust-123",
    "cifId": "CIF123456",
    "customerTypeId": 1,
    "createdBy": "admin"
  }'
```

### Get Customers with Pagination
```bash
curl "http://localhost:8080/api/v1/customers?page=0&size=10&sort=createdAt,desc"
```

### Search Customers
```bash
curl "http://localhost:8080/api/v1/customers/search?cifId=CIF123"
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is a proof-of-concept for educational purposes.

## 📞 Support

For questions or issues, please create an issue in the repository or contact the development team.

---

**Built with ❤️ using Kotlin, Spring Boot, and MongoDB**