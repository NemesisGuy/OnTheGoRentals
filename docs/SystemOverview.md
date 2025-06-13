# System Overview: OnTheGoRentals

## System Purpose

**OnTheGoRentals** is a car rental management application built using Spring Boot. It manages the entire rental lifecycle from bookings to active rentals, damage reporting, and administrative functions.

## I. System Components

The OnTheGoRentals system is composed of several key components that work together to provide its services:

*   **Backend API:**
    *   **Description:** A robust server-side application that exposes RESTful APIs to handle all business logic, data processing, and core functionalities. The API is versioned, with the current primary version being `/api/v1`.
    *   **Technology:** Built with Java using the Spring Boot framework, incorporating Spring Security for authentication/authorization and Spring Data JPA for database interaction.
*   **Frontend Single Page Application (SPA):**
    *   **Description:** An interactive and responsive user interface that allows users and administrators to interact with the system. The frontend project is maintained in a separate repository: [OnTheGoRentalsFrontend](https://github.com/NemesisGuy/OnTheGoRentalsFrontend).
    *   **Technology:** Developed using Vue.js.
*   **Database:**
    *   **Description:** The persistence layer responsible for storing all application data, including user accounts, vehicle information, rental bookings, FAQs, and system settings.
    *   **Technology:** MySQL.
*   **Docker Environment:**
    *   **Description:** The application is designed to run within Docker containers, facilitating consistent deployment across different environments and simplifying orchestration.
    *   **Technology:** Docker, Docker Compose.

## II. Core Technologies & Technology Stack

*   **Backend:** Java 21, Spring Boot 3.5.0, Spring MVC, Spring Security 6.5.0, Spring Data JPA, JWT (JSON Web Tokens)
*   **Frontend:** Vue.js
*   **Database:** MySQL (Relational DB; H2 in development; PostgreSQL/MySQL in production also noted)
*   **Build Tool:** Maven (Maven or Gradle also noted)
*   **Containerization:** Docker, Docker Compose
*   **Utilities:** Lombok

## III. User Roles and Flow

### Public User:

*   Browse available cars
*   View About Us, FAQs, Help Center

### Authenticated User:

*   Book cars
*   Manage bookings (confirm, cancel)
*   View rental history
*   Edit profile

### Admin/Staff:

*   Manage Users, Cars, Bookings, Rentals, Drivers, Damage Reports
*   Manage site content (About Us, Contact Us, FAQs, Help Center, Settings)

### Booking to Rental Flow:

1.  User makes a **Booking**
2.  Staff confirms the booking and turns it into a **Rental**
3.  User returns the car; staff marks **Rental** as returned

## IV. Key Entities

### User

*   UUID, internal ID, firstName, lastName, email, encodedPassword, roles
*   Optional Google auth, profileImageUrl
*   Associations: Roles (many-to-many), Rentals (one-to-many)

### Role

*   Internal ID, RoleName enum (USER, ADMIN, SUPERADMIN)

### Car

*   UUID, ID, make, model, year, category, PriceGroup enum, licensePlate, availability

### Booking

*   UUID, ID, user, car, booking dates, booking status

### Rental

*   UUID, ID, user, car, (optional) driver, issueDate, expectedReturnDate, actualReturnDate, status, issuerId, receiverId

### Driver

*   UUID, ID, personal/license details, availability
*   Currently optional; could represent external driver assigned by admin

### DamageReport

*   UUID, ID, description, time, location, repairCost, associated Rental
*   Can occur under staff or customer care

### ContactUs

*   UUID, ID, name, email, subject, message, submissionDate

### AboutUs / Faq / HelpCenter / Feedback / Settings

*   Static informational content and app-wide config settings

### RefreshToken

*   Associated with User, for JWT refresh

## V. Architecture Overview

The OnTheGoRentals application follows a common client-server architecture:

*   **Frontend-Backend Interaction:**
    *   The **Frontend SPA** (Vue.js) acts as the client, providing the user interface. It does not contain any core business logic.
    *   All user actions and data requests from the frontend are translated into HTTP API calls to the **Backend API**.
    *   The Backend API processes these requests, performs necessary operations, and returns responses (typically in JSON format, wrapped in a standard `ApiResponse` structure) to the frontend.
*   **Backend API Responsibilities:**
    *   The Backend API is the heart of the system. It handles:
        *   User authentication and authorization (via Spring Security and JWT); this typically involves issuing a short-lived access token in the API response and a longer-lived refresh token via a secure HTTP-only cookie.
        *   All business logic related to car rentals, bookings, user management, etc.
        *   Interaction with the **Database** for creating, reading, updating, and deleting data (CRUD operations) via Spring Data JPA.
        *   Serving content for FAQs, help topics, and other informational sections.
*   **Database:**
    *   The **MySQL Database** serves as the single source of truth for all persistent data. It stores information about users, roles, cars, bookings, rental history, administrative content, and system configurations.

```mermaid
graph LR
    User[User/Admin] -- Interacts via Browser --> FrontendSPA[Frontend SPA (Vue.js)];
    FrontendSPA -- HTTP API Calls (RESTful) --> BackendAPI[Backend API (Spring Boot)];
    BackendAPI -- CRUD Operations (JPA) --> Database[(MySQL Database)];
    BackendAPI -- Manages --> DockerEnv[Docker Environment];

    style User fill:#f9f,stroke:#333,stroke-width:2px;
    style FrontendSPA fill:#bbf,stroke:#333,stroke-width:2px;
    style BackendAPI fill:#9f9,stroke:#333,stroke-width:2px;
    style Database fill:#ff9,stroke:#333,stroke-width:2px;
    style DockerEnv fill:#ddd,stroke:#333,stroke-width:2px;
``` 

*(Note: The Mermaid diagram above provides a simplified visual representation of the interaction flow.)*

## VI. API Structure

### Base Path: `/api/v1/`

#### Public Endpoints:

*   `/cars`, `/faqs`, `/contact-us`, `/about-us`, `/help-topics`

#### Auth Endpoints:

*   `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`

#### User Profile:

*   `/users/me/profile`, `/users/me/rental-history`

#### Bookings:

*   `/bookings` (create, view, update, confirm, cancel)

#### Rentals:

*   `/rentals` (view own, confirm, cancel, complete)

#### Admin Panel:

*   `/admin/...` for managing all entities (CRUD with UUIDs)

## VII. Services

*   Services follow the `I...Service` interface and `...ServiceImpl` implementation pattern
*   Pure logic, no DTOs or ResponseEntity
*   Uses Builder pattern for updates

## VIII. Security

*   **JWT Authentication**
    *   Access Token: Short-lived, in response body
    *   Refresh Token: Long-lived, in secure HTTP-only cookies
*   **JwtUtilities:** Token creation, validation, and cookie logic
*   **Spring Security:** Role-based authorization (USER, ADMIN, SUPERADMIN)
*   **JwtAuthenticationFilter:** Parses token from requests

## IX. DTOs and Mappers

*   **DTO Types:** RequestDTOs (with @Valid), ResponseDTOs
*   **Mapper Layer:** Entity <-> DTO transformations

## X. Error Handling

*   **Custom Exceptions:** ResourceNotFound, TokenRefresh, etc.
*   **GlobalExceptionHandler:** Maps errors into `ApiResponse<T>`
*   **Response Wrapper:** Wraps success responses into standard format

## XI. Data Seeding

*   CommandLineRunner seeds USER/ADMIN roles and test users if missing or soft-deleted

## XII. Logging

*   Uses SLF4J (Logback) with user context awareness
*   Logs requester identity (email or GUEST)
*   Logging levels: INFO, DEBUG, WARN, ERROR

## XIII. Deployment

The OnTheGoRentals application is designed for deployment using **Docker containers**.
*   The backend application is packaged into a Docker image.
*   `docker-compose.yaml` files are provided to orchestrate the deployment of the application service, potentially alongside other services like the MySQL database, especially for local development and testing.
*   A `docker-compose-stack-dploy.yaml` file suggests readiness for deployment in a Docker Swarm environment for more scalable setups.

---

This document serves as the current architecture baseline of the **OnTheGoRentals** system. It provides a high-level understanding of its architecture and core components. For more detailed information, refer to the specific documentation for the API endpoints and other project files.
The previous note "before the upcoming architectural revisions and feature enhancements (e.g., incident reports outside customer custody, driver clarification, and refined rental status handling)" is retained for context.
