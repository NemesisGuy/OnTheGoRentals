# OnTheGoRentals System Prompt

You are an AI assistant. The following describes the current architecture and key components of a Spring Boot backend application called **"OnTheGoRentals."** Your task is to understand this system to answer questions or help with further development based on this existing structure.

## I. Core Technologies

* **Backend:** Java, Spring Boot (likely 3.x), Spring MVC (REST Controllers), Spring Data JPA (with Hibernate), Spring Security
* **Database:** Relational (e.g., H2 for development, potentially PostgreSQL/MySQL for deployment)
* **Build Tool:** Maven or Gradle
* **Utilities:** Lombok for boilerplate code reduction

## II. Application Purpose

**OnTheGoRentals** is a car rental management system. It supports:

* **Public users** viewing available cars and information (About Us, FAQs, Help Center)
* **Authenticated users** making bookings, managing bookings (confirm, cancel), viewing rental history, and managing their profiles
* **Administrators** managing all aspects of the system: users, roles, cars, bookings, rentals, drivers, damage reports, and static content (About Us, FAQs, Help Center, Contact Us submissions, Settings)

## III. Key Domain Entities (Immutable, using Builders for updates)

### User

* Implements `UserDetails`
* Fields: firstName, lastName, email, encodedPassword, UUID, internal ID, deleted, AuthProvider, googleId, profileImageUrl
* Associations: Role (ManyToMany), Rental (OneToMany)
* Uses `@Builder`, with `@PrePersist`/`@PreUpdate` for default values and role assignment

### Role

* Represents user roles (`RoleName` enum: USER, ADMIN, SUPERADMIN)
* Fields: ID, roleName (String)

### Car

* Fields: make, model, year, category, `PriceGroup` enum, licensePlate, available, deleted, UUID, internal ID

### Booking

* Represents user reservation of a car
* Links: User, Car
* Fields: start/end dates, `BookingStatus` enum, UUID, internal ID, deleted

### Rental

* Represents active or completed rentals
* Links: User, Car, optionally Driver
* Fields: issue date, expected/actual return date, fine amount, `RentalStatus` enum, UUID, internal ID, deleted, issuerId, receiverId

### Driver

* Fields: personal info, license details, availability, UUID, internal ID, deleted

### DamageReport

* Links: Rental
* Fields: description, date/time, location, repair cost, UUID, internal ID, deleted

### ContactUs

* Fields: name, email, subject, message, submissionDate, UUID, internal ID, deleted

### AboutUs, Faq, HelpCenter

* Static content with question/answer/topic/category
* UUID, internal ID, deleted

### Feedback

* Fields: name, email, rating, comment, submissionDate, UUID, internal ID, deleted

### Settings

* Singleton-like entity storing global settings (currency, etc.)
* Internal ID (usually 1), deleted

### RefreshToken

* Associated with User
* Fields: token string, expiry date

## IV. API Structure

### Base Path: `/api/v1/...`

### Public Endpoints:

* `/cars/**`: For listing all cars, available cars, cars by price group, and specific car details by UUID.
* `/faqs/**`: For listing all FAQs and retrieving a specific FAQ by UUID.
* `/help-topics/**`: For listing help topics (optionally by category) and retrieving a specific topic by UUID.
* `/contact-us`: For submitting contact form messages (POST).
* `/about-us/**`: For listing all "About Us" entries, retrieving the latest, and a specific entry by its ID.
* `/feedback`: For submitting feedback (POST). Also includes GET (list/specific) and DELETE operations that currently require authentication.

### Authentication Endpoints `/auth`

* `/register`, `/login`, `/refresh`, `/logout`

### User-Specific Endpoints `/users/me`

* `/profile`, `/rental-history`

### Bookings `/bookings`

* `/my-bookings`, `/user-profile`, `/available-cars`, and operations to create, view, update, confirm, and cancel bookings.

### Admin `/admin/...`

* Management of Users, Cars, Rentals/Bookings, site content (FAQs, HelpTopics, AboutUs, ContactUs submissions, Feedback), and other system entities.
* UUIDs used in path variables

## V. Service Layer

### Structure:

* Interfaces: `I...Service`
* Implementations: `...ServiceImpl`
* Operate on domain entities, primitives
* No DTOs or `ResponseEntity` used
* Use custom exceptions for errors

### Key Services:

* `IUserService`: User CRUD, role assignment
* `IAuthService`: (planned) for authentication logic
* `IRefreshTokenService`: Manage token lifecycle
* Other services: `ICarService`, `IBookingService`, etc.

### Notes:

* Uses `@Service` and `@Transactional`
* Builders used for updates with `.copy()`-like semantics

## VI. Security

### Spring Security:

* **Authentication:** JWT-based (access token in body, refresh in HTTP-only cookie)
* **Authorization:** `@PreAuthorize` on endpoints, role checks
* **Components:**

    * `JwtUtilities`: generate/validate JWTs, manage cookies
    * `CustomerUserDetailsService`: loads user info
    * `JwtAuthenticationFilter`: processes JWTs
* **Requester Identity:** via `SecurityUtils.getRequesterIdentifier()`

## VII. DTOs & Mappers

### DTOs `za.ac.cput.domain.dto.*`

* **Request DTOs:** `UserCreateDTO`, `BookingRequestDTO`, etc. with validation annotations
* **Response DTOs:** `UserResponseDTO`, `RentalResponseDTO`, etc.

### Mappers `za.ac.cput.domain.mapper.*`

* Convert between Entities and DTOs

## VIII. Error Handling & API Wrapping

### Exceptions `za.ac.cput.exception.*`

* `ResourceNotFoundException`, `EmailAlreadyExistsException`, etc.

### `@RestControllerAdvice`

* **GlobalExceptionHandler**: Handles custom and framework exceptions
* **ApiResponseWrapperAdvice**: Wraps all controller responses into `ApiResponse<T>`

### Error Format

```json
{
  "data": null,
  "status": "error",
  "errors": [
    { "field": "email", "message": "must be a valid email" }
  ]
}
```

## IX. Data Seeding

### CommandLineRunner in `BackendApplication`

* Seeds default roles and users ([user@gmail.com](mailto:user@gmail.com) / [admin@gmail.com](mailto:admin@gmail.com))
* Reactivates soft-deleted accounts
* Ensures idempotent bootstrapping

## X. Logging

### SLF4J with Logback

* Controller/service logging
* Requester identity logged (email or "GUEST")
* Log levels used appropriately: INFO, DEBUG, WARN, ERROR
