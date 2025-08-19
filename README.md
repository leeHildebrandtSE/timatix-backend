# 🚗 Timatix Booking Services Backend

A comprehensive vehicle service management and booking system backend built with Spring Boot.

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## ✨ Features

### Core Functionality
- **User Management**: Client, Mechanic, and Admin roles
- **Vehicle Management**: Add, edit, and manage vehicle profiles
- **Service Catalog**: Predefined and custom services
- **Quote-Based Workflow**: Request → Quote → Approval → Booking
- **Booking Slots**: Calendar-based scheduling system
- **Progress Tracking**: Real-time service status updates
- **Comprehensive APIs**: RESTful endpoints for all operations

### Technical Features
- **Spring Boot 3.5.4** with Java 21
- **PostgreSQL** database with optimized schema
- **Global Exception Handling** with detailed error responses
- **CORS Configuration** for frontend integration
- **Health Checks** and monitoring endpoints
- **Docker Support** for easy deployment
- **Comprehensive Testing** with JUnit 5
- **Data Validation** with Bean Validation
- **Automatic Data Initialization** with sample data

## 🏗️ Architecture

```
├── src/main/java/com/timatix/servicebooking/
│   ├── controller/          # REST Controllers
│   ├── service/            # Business Logic Layer
│   ├── repository/         # Data Access Layer
│   ├── model/              # Entity Models
│   ├── dto/                # Data Transfer Objects
│   ├── config/             # Configuration Classes
│   └── exception/          # Exception Handling
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql          # Database Schema
│   └── data.sql           # Sample Data
└── src/test/              # Test Classes
```

## 🚀 Quick Start

### Prerequisites
- **Java 21** or higher
- **PostgreSQL 12+**
- **Maven 3.6+**

### Using Docker (Recommended)
```bash
# Clone the repository
git clone https://github.com/your-username/timatix-backend.git
cd timatix-backend

# Start with Docker Compose
docker-compose up -d

# The API will be available at http://localhost:8081/api
```

### Manual Setup
```bash
# 1. Setup PostgreSQL database
createdb timatix_booking

# 2. Update database configuration in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/timatix_booking
spring.datasource.username=your_username
spring.datasource.password=your_password

# 3. Build and run
./mvnw clean install
./mvnw spring-boot:run

# 4. Access the application
curl http://localhost:8081/api/health
```

## 📚 API Documentation

### Base URL
```
http://localhost:8081/api
```

### Authentication
Currently using simple email/password authentication. Default users:
- **Admin**: `admin@timatix.com` / `admin123`
- **Mechanic**: `mechanic@timatix.com` / `mechanic123`

### Core Endpoints

#### User Management
```http
POST   /users/register          # Register new user
POST   /users/login             # User login
GET    /users                   # Get all users
GET    /users/mechanics         # Get all mechanics
GET    /users/clients           # Get all clients
```

#### Vehicle Management
```http
POST   /vehicles                # Add vehicle
GET    /vehicles/owner/{id}     # Get user's vehicles
PUT    /vehicles/{id}           # Update vehicle
DELETE /vehicles/{id}           # Delete vehicle
```

#### Service Catalog
```http
GET    /services                # Get all services
GET    /services/active         # Get active services only
POST   /services                # Create service (Admin only)
PUT    /services/{id}           # Update service
```

#### Booking Management
```http
GET    /booking-slots/available                    # Get available slots
GET    /booking-slots/available/date/{date}        # Get slots for date
POST   /booking-slots                              # Create slot
```

#### Service Requests
```http
POST   /service-requests                           # Create service request
GET    /service-requests/client/{clientId}         # Get client's requests
GET    /service-requests/pending-quotes            # Get pending quotes
PUT    /service-requests/{id}/assign-mechanic/{mechanicId}  # Assign mechanic
```

#### Service Quotes
```http
POST   /service-quotes/request/{requestId}/mechanic/{mechanicId}  # Create quote
PUT    /service-quotes/{id}/approve                               # Approve quote
PUT    /service-quotes/{id}/decline                               # Decline quote
GET    /service-quotes/pending                                    # Get pending quotes
```

### Complete Workflow Example

1. **Register Client and Add Vehicle**
```bash
# Register client
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@email.com",
    "password": "password123",
    "role": "CLIENT"
  }'

# Add vehicle
curl -X POST http://localhost:8081/api/vehicles/owner/1 \
  -H "Content-Type: application/json" \
  -d '{
    "make": "Toyota",
    "model": "Camry",
    "year": "2020",
    "licensePlate": "CA123GP"
  }'
```

2. **Create Service Request**
```bash
curl -X POST http://localhost:8081/api/service-requests \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "vehicleId": 1,
    "serviceId": 1,
    "preferredDate": "2025-08-25",
    "preferredTime": "09:00:00",
    "notes": "Car needs oil change"
  }'
```

3. **Mechanic Creates Quote**
```bash
curl -X POST http://localhost:8081/api/service-quotes/request/1/mechanic/2 \
  -H "Content-Type: application/json" \
  -d '{
    "labourCost": 300.00,
    "partsCost": 150.00,
    "totalAmount": 450.00,
    "notes": "Standard oil change service"
  }'
```

4. **Client Approves Quote**
```bash
curl -X PUT http://localhost:8081/api/service-quotes/1/approve
```

## 🛠️ Development

### Project Structure
```
com.timatix.servicebooking/
├── controller/              # REST API Controllers
│   ├── UserController
│   ├── VehicleController
│   ├── ServiceCatalogController
│   ├── BookingSlotController
│   ├── ServiceRequestController
│   ├── ServiceQuoteController
│   └── HealthController
├── service/                 # Business Logic
│   ├── UserService
│   ├── VehicleService
│   ├── ServiceCatalogService
│   ├── BookingSlotService
│   ├── ServiceRequestService
│   └── ServiceQuoteService
├── repository/              # Data Access
│   └── [Entity]Repository interfaces
├── model/                   # JPA Entities
│   ├── User
│   ├── Vehicle
│   ├── ServiceCatalog
│   ├── BookingSlot
│   ├── ServiceRequest
│   └── ServiceQuote
├── dto/                     # Data Transfer Objects
├── config/                  # Configuration
│   ├── CorsConfig
│   └── ApplicationConfig
└── exception/               # Exception Handling
    ├── GlobalExceptionHandler
    └── ErrorResponse
```

### Database Schema
- **Users**: Authentication and role management
- **Vehicles**: Vehicle information linked to owners
- **Service Catalog**: Available services with pricing
- **Booking Slots**: Time slot availability management
- **Service Requests**: Client service requests
- **Service Quotes**: Mechanic quotes for requests

### Adding New Features
1. Create entity model in `model/`
2. Add repository interface in `repository/`
3. Implement service logic in `service/`
4. Create controller endpoints in `controller/`
5. Add DTOs if needed in `dto/`
6. Write tests in `src/test/`

### Configuration Profiles
- **dev**: Development with debug logging
- **test**: Testing with H2 in-memory database
- **prod**: Production with security settings

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Categories
- **Unit Tests**: Service layer logic testing
- **Integration Tests**: Full application context testing
- **API Tests**: REST endpoint testing
- **Repository Tests**: Database interaction testing

### Sample Test Data
The application automatically loads sample data including:
- Admin and mechanic users
- Complete service catalog
- 30 days of booking slots
- Sample vehicles and service requests

## 🚀 Deployment

### Docker Deployment
```bash
# Build image
docker build -t timatix-backend .

# Run with Docker Compose
docker-compose up -d

# Scale services
docker-compose up -d --scale backend=3
```

### Production Checklist
- [ ] Enable HTTPS/SSL
- [ ] Configure proper database connection pooling
- [ ] Set up monitoring and logging
- [ ] Configure CORS for production domains
- [ ] Enable security features (JWT, rate limiting)
- [ ] Set up database backups
- [ ] Configure file upload storage
- [ ] Set up health checks and alerts

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/timatix_booking
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Profile
SPRING_PROFILES_ACTIVE=prod

# Server
SERVER_PORT=8081
```

## 📊 Monitoring

### Health Endpoints
- **Health Check**: `GET /health`
- **Application Info**: `GET /health/info`
- **Readiness**: `GET /health/ready`
- **Liveness**: `GET /health/live`

### Metrics
Available through Spring Boot Actuator:
- JVM metrics
- Database connection metrics
- HTTP request metrics
- Custom business metrics

## 🔐 Security Considerations

### Current Implementation
- Basic email/password authentication
- Role-based access (CLIENT, MECHANIC, ADMIN)
- Input validation with Bean Validation
- SQL injection prevention with JPA
- CORS configuration for frontend integration

### Production Recommendations
- Implement JWT authentication
- Add password hashing (BCrypt)
- Enable HTTPS/TLS
- Add rate limiting
- Implement API key authentication
- Set up proper logging and monitoring
- Add input sanitization
- Configure security headers

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Write tests for new features
- Update documentation
- Use meaningful commit messages
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, email support@timatix.com or create an issue in the repository.

---

**Built with ❤️ by the Timatix Team**