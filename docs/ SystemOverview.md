# OnTheGoRentals System Overview

## System Purpose

**OnTheGoRentals** is a car rental management application built using Spring Boot. It manages the entire rental lifecycle from user authentication and booking to active rentals, damage reporting, and comprehensive administrative functions.

## I. High-Level Features

*   **Multi-Faceted Authentication:** Standard email/password registration, secure password reset, and one-click Google OAuth2 login.
*   **Full Rental Lifecycle Management:** Users can book cars; administrators can manage the fleet, confirm bookings, handle rentals, and log damage reports.
*   **Comprehensive Admin Dashboard:** A central hub for managing all aspects of the application, including users, cars, content, and system health.
*   **Transactional Email Notifications:** Automated, templated HTML emails for user registration and password resets.
*   **Full Observability Stack:** Integrated suite for metrics (Prometheus), logging (Loki), and visualization (Grafana).

## II. Core Technologies

*   **Backend:** Java, Spring Boot, Spring MVC, Spring Security (JWT & OAuth2), Spring Data JPA, Thymeleaf
*   **Database:** Relational DB (H2 in development; MySQL in production)
*   **Build Tool:** Maven
*   **Observability:** Prometheus, Grafana, Loki
*   **Utilities:** Lombok

## III. User Roles and Flow

### Public User:

*   Browse available cars.
*   View About Us, FAQs, Help Center.
*   Register via email/password or Google account.
*   Request a password reset.

### Authenticated User:

*   Book cars.
*   Manage personal bookings (confirm, cancel).
*   View rental history.
*   Edit profile information.

### Admin/Staff:

*   Manage Users, Cars, Bookings, Rentals, Drivers, Damage Reports.
*   Manage site content (About Us, Contact Us, FAQs, Help Center, Settings).

### Booking to Rental Flow:

1.  User makes a **Booking**.
2.  Staff confirms the booking and converts it into a **Rental**.
3.  User returns the car; staff marks the **Rental** as returned.

## IV. Key Entities

### User

*   UUID, internal ID, firstName, lastName, email, encodedPassword, roles.
*   Optional Google auth, profileImageUrl.
*   **Password Reset Fields:** `passwordResetToken`, `passwordResetTokenExpiry`.
*   Associations: Roles (many-to-many), Rentals (one-to-many).

### Role

*   Internal ID, RoleName enum (USER, ADMIN, SUPERADMIN).

### Car

*   UUID, ID, make, model, year, category, PriceGroup enum, licensePlate, availability.

### Booking / Rental

*   Manages the reservation and active rental states with associations to User and Car, tracking relevant dates and statuses.

### Other Core Entities

*   **Driver:** Optional driver details for a rental.
*   **DamageReport:** Details any damage associated with a rental.
*   **RefreshToken:** Securely manages JWT refresh capabilities for a user.
*   **Content Entities:** AboutUs, Faq, HelpCenter, etc., for managing site content.

## V. API Structure

### Base Path: `/api/v1/`

#### Public Endpoints:

*   `/cars/**`, `/faqs/**`, `/contact-us`, `/about-us/**`, `/help-center/**`

#### Auth Endpoints:

*   `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`
*   `/auth/forgot-password`, `/auth/reset-password`
*   `/oauth2/authorization/google` (Handled by Spring Security)

#### User Profile:

*   `/users/me/profile`, `/users/me/rental-history`

#### Bookings & Rentals:

*   `/bookings/**`, `/rentals/**`

#### Admin Panel:

*   `/admin/...` for managing all entities (CRUD with UUIDs).

## VI. Services

*   Services follow the `I...Service` interface and `...ServiceImpl` implementation pattern.
*   Encapsulate business logic; do not interact with DTOs or `ResponseEntity`.
*   Use the Builder pattern for immutable updates.

## VII. Security

*   **JWT Authentication:**
  *   Access Token: Short-lived, returned in the response body.
  *   Refresh Token: Long-lived, stored in a secure, HttpOnly cookie.
*   **Google OAuth2:** Integrated into the Spring Security filter chain, with custom success handlers to provision users and issue application-specific JWTs.
*   **JwtAuthenticationFilter:** The primary filter that parses JWTs from incoming requests to authenticate users.

## VIII. DTOs and Mappers

*   **DTO Types:** RequestDTOs (for input, using `@Valid`) and ResponseDTOs (for output).
*   **Mapper Layer:** Stateless utility classes responsible for Entity <-> DTO transformations.

## IX. Error Handling

*   **Custom Exceptions:** A suite of specific exceptions like `ResourceNotFoundException`, `TokenRefreshException`, `BadRequestException`.
*   **GlobalExceptionHandler:** A `@RestControllerAdvice` component that catches exceptions and maps them to a standard `ApiResponseWrapper`, ensuring consistent error responses.
*   **Response Wrapper:** A custom advice that wraps all successful API responses in the same standard envelope for consistency.

## X. Data Seeding

*   A `CommandLineRunner` seeds the database with default `USER`/`ADMIN` roles and test user accounts on startup if they do not already exist.

## XI. Transactional Emails

*   **IEmailService:** An abstraction for sending emails.
*   **EmailServiceImpl:** An `@Async` (non-blocking) implementation that sends emails via an external SMTP relay service (e.g., Brevo).
*   **Thymeleaf:** Used to create dynamic, reusable HTML templates for professional-looking emails (e.g., welcome messages, password reset instructions).

## XII. Observability & Monitoring

*   **Metrics:** Spring Boot Actuator exposes application metrics via a `/actuator/prometheus` endpoint.
*   **Prometheus:** A time-series database configured to periodically scrape and store these metrics.
*   **Logging:** Centralized log aggregation is handled by **Loki**, with **Promtail** acting as the agent to collect logs from all Docker containers.
*   **Visualization:** **Grafana** serves as the unified dashboard for visualizing both Prometheus metrics and Loki logs, enabling powerful correlation between performance spikes and application log events.

---

This document serves as the current architecture baseline of the **OnTheGoRentals** system.