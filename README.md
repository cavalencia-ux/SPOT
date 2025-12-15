# SPOT - Smart Parking Optimization for Tenants

A full-stack web application for managing parking lots with real-time vehicle tracking, booking, and analytics.

## Features

- ✅ **Check-In/Check-Out**: Register and release parking spaces
- ✅ **Real-time Dashboard**: Monitor parking lot occupancy
- ✅ **Vehicle Search**: Find parked vehicles by plate number
- ✅ **Waiting List**: Queue system for when lot is full
- ✅ **Parking Duration Tracking**: Calculate parking time
- ✅ **Occupancy Analytics**: Real-time occupancy rates
- ✅ **Responsive Design**: Works on desktop and mobile

## Data Structures Used

1. **HashMap** - Quick lookup of parking assignments (vehicle plate → spot number)
2. **ArrayList** - Dynamic storage of parked vehicles with sorting
3. **Queue** - Waiting list for vehicles when parking lot is full
4. **Database (JPA)** - Persistent storage of parking records

## System Architecture

### Backend
- **Framework**: Spring Boot 3.1.0
- **Database**: H2 (In-memory)
- **Language**: Java 17
- **Build Tool**: Maven

### Frontend
- **HTML5** - Markup
- **CSS3** - Styling with responsive design
- **JavaScript (ES6)** - Interactive functionality
- **REST API** - Communication with backend

## Project Structure

```
SPOT/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/spot/
│   │   │   ├── SpotApplication.java         # Main application class
│   │   │   ├── controller/
│   │   │   │   └── ParkingController.java   # REST API endpoints
│   │   │   ├── service/
│   │   │   │   └── ParkingLotService.java   # Business logic
│   │   │   ├── repository/
│   │   │   │   └── ParkingTicketRepository.java  # Database access
│   │   │   └── model/
│   │   │       └── ParkingTicket.java       # Data model
│   │   └── resources/
│   │       ├── application.properties       # Spring config
│   │       └── static/
│   │           ├── index.html              # Frontend UI
│   │           ├── style.css               # Styling
│   │           └── script.js               # Frontend logic
│   └── test/
└── README.md
```

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8 or higher

### Steps

1. **Clone/Navigate to project**
   ```bash
   cd c:\Users\ryzen\Desktop\SPOT
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Open browser: `http://localhost:8080`
   - The frontend will load automatically

## API Endpoints

### Check In Vehicle
```
POST /api/parking/check-in
Parameters: tenantName, vehiclePlate
Response: {success, message, ticket}
```

### Check Out Vehicle
```
POST /api/parking/check-out
Parameters: spotNumber
Response: {success, message, ticket, durationHours, durationMinutes}
```

### Get Parking Status
```
GET /api/parking/status
Response: {totalSpots, occupiedSpots, availableSpots, vehicles, occupancyRate}
```

### Search Vehicle
```
GET /api/parking/search
Parameters: vehiclePlate
Response: ParkingTicket or {message}
```

### Get Waiting List
```
GET /api/parking/waiting-list
Response: [List of waiting vehicles]
```

### Get All Vehicles
```
GET /api/parking/vehicles
Response: [List of ParkingTicket objects]
```

## Usage Guide

### Dashboard Tab
- View real-time parking lot status
- See occupancy rate and available spots
- Monitor all currently parked vehicles

### Check In Tab
- Enter tenant name and vehicle plate
- Vehicle is automatically assigned to next available spot
- Added to waiting list if lot is full

### Check Out Tab
- Enter the spot number
- System calculates parking duration
- Generates checkout report

### Search Tab
- Search for any vehicle by plate number
- View parking details and check-in time

### Waiting List Tab
- View all vehicles waiting for spots
- Shows queue position

## Data Persistence

The application uses:
- **H2 Database** - In-memory database (data resets on restart)
- **JPA/Hibernate** - ORM for database operations

To use persistent storage, modify `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./parking_data
spring.jpa.hibernate.ddl-auto=update
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=SPOT-Parking-System
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:spotdb
spring.datasource.driverClassName=org.h2.Driver
server.port=8080
```

## Technologies & Concepts Demonstrated

### OOP Principles
- Encapsulation (private fields with getters/setters)
- Inheritance (Spring components)
- Polymorphism (Service layer abstraction)

### Design Patterns
- MVC Pattern (Model-View-Controller)
- Repository Pattern (Data access abstraction)
- Service Layer Pattern (Business logic separation)

### Data Structures
- HashMap for O(1) vehicle lookup
- ArrayList for vehicle listing with sorting
- Queue for FIFO waiting list management

### Algorithms
- Linear search for available spots (can be optimized)
- Spot allocation using first-available strategy
- Time calculation using ChronoUnit

## Future Enhancements

- [ ] User authentication and authorization
- [ ] Payment system integration
- [ ] Email notifications
- [ ] Reserved parking spots
- [ ] Pricing and billing
- [ ] Admin dashboard
- [ ] Monthly reports and analytics
- [ ] Mobile app development

## Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### Database Issues
```bash
# Clear data and restart
# H2 database is in-memory, so just restart the application
```

### CORS Issues
The API has CORS enabled for all origins. If needed, modify:
```java
@CrossOrigin(origins = "YOUR_DOMAIN")
```

## License

This is an educational project for demonstration purposes.

## Author

SPOT Development Team - 2025

---

**For any issues or questions, refer to the source code comments and API documentation.**
