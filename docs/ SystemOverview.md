# OnTheGoRentals System Overview

## System Purpose

**OnTheGoRentals** is a car rental management application built using Spring Boot. It manages the entire rental lifecycle from bookings to active rentals, damage reporting, and administrative functions.

## I. Core Technologies

* **Backend:** Java, Spring Boot, Spring MVC, Spring Security, Spring Data JPA
* **Database:** Relational DB (H2 in development; PostgreSQL/MySQL in production)
* **Build Tool:** Maven or Gradle
* **Utilities:** Lombok

## II. User Roles and Flow

### Public User:

* Browse available cars
* View About Us, FAQs, Help Center

### Authenticated User:

* Book cars
* Manage bookings (confirm, cancel)
* View rental history
* Edit profile

### Admin/Staff:

* Manage Users, Cars, Bookings, Rentals, Drivers, Damage Reports
* Manage site content (About Us, Contact Us, FAQs, Help Center, Settings)

### Booking to Rental Flow:

1. User makes a **Booking**
2. Staff confirms the booking and turns it into a **Rental**
3. User returns the car; staff marks **Rental** as returned

## III. Key Entities

### User

* UUID, internal ID, firstName, lastName, email, encodedPassword, roles
* Optional Google auth, profileImageUrl
* Associations: Roles (many-to-many), Rentals (one-to-many)

### Role

* Internal ID, RoleName enum (USER, ADMIN, SUPERADMIN)

### Car

* UUID, ID, make, model, year, category, PriceGroup enum, licensePlate, availability

### Booking

* UUID, ID, user, car, booking dates, booking status

### Rental

* UUID, ID, user, car, (optional) driver, issueDate, expectedReturnDate, actualReturnDate, status, issuerId, receiverId

### Driver

* UUID, ID, personal/license details, availability
* Currently optional; could represent external driver assigned by admin

### DamageReport

* UUID, ID, description, time, location, repairCost, associated Rental
* Can occur under staff or customer care

### ContactUs

* UUID, ID, name, email, subject, message, submissionDate

### AboutUs / Faq / HelpCenter / Feedback / Settings

* Static informational content and app-wide config settings

### RefreshToken

* Associated with User, for JWT refresh

## IV. API Structure

### Base Path: `/api/v1/`

#### Public Endpoints:

* `/cars`, `/faqs`, `/contact-us`, `/about-us`, `/help-topics`

#### Auth Endpoints:

* `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`

#### User Profile:

* `/users/me/profile`, `/users/me/rental-history`

#### Bookings:

* `/bookings` (create, view, update, confirm, cancel)

#### Rentals:

* `/rentals` (view own, confirm, cancel, complete)

#### Admin Panel:

* `/admin/...` for managing all entities (CRUD with UUIDs)

## V. Services

* Services follow the `I...Service` interface and `...ServiceImpl` implementation pattern
* Pure logic, no DTOs or ResponseEntity
* Uses Builder pattern for updates

## VI. Security

* **JWT Authentication**

    * Access Token: Short-lived, in response body
    * Refresh Token: Long-lived, in secure HTTP-only cookies

* **JwtUtilities:** Token creation, validation, and cookie logic

* **Spring Security:** Role-based authorization (USER, ADMIN, SUPERADMIN)

* **JwtAuthenticationFilter:** Parses token from requests

## VII. DTOs and Mappers

* **DTO Types:** RequestDTOs (with @Valid), ResponseDTOs
* **Mapper Layer:** Entity <-> DTO transformations

## VIII. Error Handling

* **Custom Exceptions:** ResourceNotFound, TokenRefresh, etc.
* **GlobalExceptionHandler:** Maps errors into `ApiResponse<T>`
* **Response Wrapper:** Wraps success responses into standard format

## IX. Data Seeding

* CommandLineRunner seeds USER/ADMIN roles and test users if missing or soft-deleted

## X. Logging

* Uses SLF4J (Logback) with user context awareness
* Logs requester identity (email or GUEST)
* Logging levels: INFO, DEBUG, WARN, ERROR

---

This document serves as the current architecture baseline of the **OnTheGoRentals** system before the upcoming architectural revisions and feature enhancements (e.g., incident reports outside customer custody, driver clarification, and refined rental status handling).
