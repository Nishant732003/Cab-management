# Auth Feature Branch

**Introduction**

This is the backend repository for the Cab Booking Platform, built using Spring Boot. The backend provides RESTful APIs for managing user authentication, admin verification, and cab booking operations.

**Prerequisites**

To run the server without errors, ensure you have the following:

1. **Java 11 or later**: The backend is built using Java 11, so make sure you have the latest version installed.
2. **Maven 3.6 or later**: The project uses Maven as the build tool, so ensure you have the latest version installed.
3. **MySQL 8 or later**: The backend uses MySQL as the database management system, so make sure you have the latest version installed.
4. **Spring Boot 2.5 or later**: The backend is built using Spring Boot 2.5, so ensure you have the latest version installed.

**Starting the Server**

To start the server, follow these steps:

1. Clone the repository: `git clone https://github.com/your-repo/cab-booking-backend.git`
2. Navigate to the project directory: `cd cab-booking-backend`
3. Build the project using Maven: `mvn clean install`
4. Run the application using Spring Boot: `mvn spring-boot:run`

**API Endpoints**

The backend provides the following API endpoints:

1. **Auth APIs**
   - `POST /api/auth/register/admin`: Registration endpoint for new admins
   - `POST /api/auth/register/driver`: Registration endpoint for new drivers
   - `POST /api/auth/register/customer`: Registration endpoint for new customers
   - `POST /api/auth/login`: Login endpoint for users
   - `POST /api/auth/logout`: Logout endpoint for users
2. **Cab APIs**
   - `POST /api/cabs/add`: Add a new Cab
   - `PUT /api/cabs/update`: Update existing Cab details
   - `DELETE /api/cabs/delete/{cabId}`: Delete existing Cab
   - `GET /api/cabs/view/{carType}`: Get all Cabs of a particular carType
   - `GET /api/cabs/count/{carType}`: Get count of all Cabs of a particular carType
3. **Trip Booking APIs**
   - `POST /api/trips/book`: Book a new Trip from a source to destination
   - `PUT /api/trips/{tripId}/status?status=status`: Change the status of a trip
   - `PUT /api/trips/{tripId}/complete`: Complete/End a trip
   - `GET /api/trips/customer/{customerId}`: Get all trips of a customer
   - `POST /api/trips/{tripId}/rate`: Rate a trip
4. **Admin APIs**
   - `GET /api/admin/unverified/drivers`: Get list of all unverified drivers
   - `POST /api/admin/verify/drivers/{driverId}`: Verify a driver
   - `GET /api/admin/unverified/admins`: Get list of all unverified admins
   - `POST /api/admin/verify/admins/{adminId}`: Verify an admin

**Swagger API Documentation**

The URL to access the Swagger API Documentation - http://localhost:8081/swagger-ui/index.html

**Security**

The backend uses JSON Web Tokens (JWT) for authentication and authorization. The `JwtUtil` class provides methods for generating, validating, and parsing JWT tokens.

**Database Configuration**

The backend uses MySQL as the database management system. The database configuration is defined in the `application.properties` file.

**Logging**

The backend uses Logback for logging. The logging configuration is defined in the `logback.xml` file.

**Troubleshooting**

If you encounter any errors while running the server, check the following:

1. Ensure that the MySQL database is running and the credentials are correct.
2. Check the logging configuration to ensure that logs are being written to the correct location.
3. Verify that the JWT secret key is correctly configured in the `JwtUtil` class.
