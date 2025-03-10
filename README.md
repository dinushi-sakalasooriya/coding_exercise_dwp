
# DWP Ticket Service Application

Application update - Java Software Engineer - 381321 (campaign number)
Application ID number: 13233091

This is a Spring Boot application for managing ticket purchases, including validating business rules, calculating ticket costs, and handling seat reservations and payments. The service ensures compliance with ticket limits and validates that the necessary ticket types are included in a purchase.

## Features
- **Ticket Purchase**: Supports purchasing Adult, Child, and Infant tickets.
- **Business Rules Enforcement**: Ensures that at least one Adult ticket is purchased, the total ticket count does not exceed the limit, and that valid account IDs are provided.
- **Payment Integration**: Processes payments using the `TicketPaymentService`.
- **Seat Reservation**: Reserves seats using the `SeatReservationService`.

## Test Cases
This project includes 13 unit tests that validate the core functionality of the `TicketService` implementation.

## Setup
### Prerequisites
- Java 11
- Spring Boot
- Maven

### Running the Application
To build and run the application, use the following command:
```bash```
mvn clean install
mvn spring-boot:run
``````
### Running Tests
To run the tests, use:

```bash```
mvn test
``````````


