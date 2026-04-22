# Plane Management System - Project Information

## Project Overview

A comprehensive Plane Management System built in Java that demonstrates professional software engineering practices including ticket management, design patterns, reflection, and functional programming paradigms.

---

##  Technical Specifications

### SDK version
- This project uses SDK "23.0.1"
### IDE
- IntelliJ IDEA, although a VS Code extension named codesnap had been used to take clear screenshots
- Project structure compatible with IntelliJ's `.iml` format

### Build System
- Manual Compilation using `javac`
- No build tool dependencies (Maven/Gradle not required)
- Simple classpath-based execution


## Dependencies

### Core Dependencies
This project has ZERO external dependencies. It uses only:
- **Java Standard Library (JDK)**
  - `java.util.*` - Collections, Optional, Scanner
  - `java.io.*` - File I/O operations
  - `java.lang.reflect.*` - Reflection API
  - `java.util.stream.*` - Stream API for functional programming
  - `java.util.function.*` - Functional interfaces (Predicate, Function, Consumer)

---

##  Architecture & Design

### Project Structure
```
planeManagementSystem/
├── src/                          # Source code
│   ├── PlaneManagement.java      # Application entry point
│   ├── annotation/               # Custom annotations
│   │   ├── PatternInfo.java      # Documents design patterns
│   │   └── FunctionalComponent.java  # Marks functional methods
│   ├── config/                   # Configuration
│   │   └── Constants.java        # Application constants
│   ├── pricing/                  # Strategy Pattern implementation
│   │   ├── PricingStrategy.java
│   │   ├── StandardPricingStrategy.java
│   │   ├── DynamicPricingStrategy.java
│   │   └── PricingStrategyFactory.java
│   ├── model/                    # Domain models
│   │   ├── Ticket.java           # Immutable ticket entity
│   │   └── Passenger.java        # Passenger info
│   ├── service/                  # Business logic layer
│   │   ├── ITicketRepository.java
│   │   ├── TicketRepository.java
│   │   └── TicketService.java    # Service layer
│   ├── ui/                       # User interface
│   │   └── PlaneConsoleUI.java   # Console-based UI
│   ├── util/                     # Utilities
│   │   └── TicketDataManager.java  # Singleton for file operations
│   ├── reflection/               # Reflection utilities
│   │   └── ReflectionInspector.java
│   └── test/                     # Test suites
│       ├── PricingStrategyTest.java
│       ├── ReflectionTest.java
│       └── FunctionalProgrammingTest.java
├── out/                          # Compiled classes (generated)
├── tickets.json                  # Data persistence file
└── README.md                     # This file
```

### Lambda Expressions
Usage Count: 40+ lambdas

### Method References
Usage Count: 25+ method references

### Pure Functions
All pricing calculations are pure (marked with `@FunctionalComponent`):

### Immutability
Domain models are immutable:

---


## How to Run


- Install Java JDK 23 or higher
- Navigate to src directory
- Compile all Java files
- Navigate back to project root
- Run the application by using the command java -cp out PlaneManagement


### Run Tests
- Test pricing strategies by running the command
java -cp out test.PricingStrategyTest

- Test reflection by running the command
java -cp out test.ReflectionTest

- Test functional programming by running the command
java -cp out test.FunctionalProgrammingTest


---

##  Features

### Core Functionality
**Buy Ticket** - Purchase a seat with validation
**Cancel Ticket** - Cancel a purchased seat
**View Seating Plan** - Display all seats and their status
**Search Ticket** - Find ticket by seat/row
**Statistics** - Total sales, tickets sold

### Advanced Features
**JSON Persistence** - Auto-save to `tickets.json`
**Data Validation** - Input validation with clear errors
**Pricing Calculation** - Automatic with strategy pattern
**Sorting/Filtering** - By seat, row, price
**Reflection Analysis** - Startup inspection report

---


##  Data Format

### tickets.json Structure
```
{
  "tickets": [
    {
      "row": "A",
      "seat": 1,
      "price": 200,
      "passenger": {
        "name": "John",
        "surname": "Doe",
        "email": "john.doe@email.com"
      }
    }
  ]
}
```