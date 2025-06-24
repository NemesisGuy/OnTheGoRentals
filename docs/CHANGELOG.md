# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v0.2.0-beta] - 2025-06-24

### ✨ Added

-   **Google OAuth2 Login:** Implemented a complete "Login with Google" flow.
    -   Users can now register and log in using their Google account.
    -   Backend handles user creation for new OAuth logins and session management for existing ones.
    -   Includes custom OAuth2 success handler to integrate with the application's JWT-based authentication system.
-   **Forgot Password & Reset Flow:** Implemented a full password reset feature.
    -   Users can request a password reset link to be sent to their email.
    -   Backend generates a secure, single-use, time-limited token.
    -   New frontend pages created to handle the request and reset forms.
-   **Transactional Email Service:** Integrated a robust email sending service.
    -   Added `spring-boot-starter-mail` and `spring-boot-starter-thymeleaf` for sending templated HTML emails.
    -   Created `IEmailService` and a non-blocking, `@Async` implementation.
    -   Sends a "Welcome" email upon user registration and a "Password Reset" email on request.
    -   Configured for a professional email relay service (like Brevo) to ensure high deliverability, suitable for any hosting environment.
-   **Observability Stack (Prometheus, Grafana, Loki):**
    -   Integrated Spring Boot Actuator with a Prometheus endpoint.
    -   Added Loki for centralized log aggregation.
    -   Set up a complete Docker Compose stack including Prometheus for metrics, Grafana for visualization, and Promtail for log collection.
    -   Created custom parsing pipelines in Promtail to structure Spring Boot logs, making them searchable and filterable by log level.

### ♻️ Changed

-   **Refactored `AuthServiceImpl`:** Moved "Welcome Email" logic from `UserService` to `AuthService` to better align with the "Registration" business process.
-   **Refactored `CarServiceImpl`:** Consolidated several redundant methods for fetching available cars into a single, efficient method.
-   **Improved API Error Handling:** All backend exceptions are now consistently wrapped in a structured `ApiResponseWrapper` for easier frontend consumption.
-   **Improved Configuration Management:** Centralized local development configuration in `application-secrets.properties`, which is git-ignored, separating secrets from tracked code.

### 🐛 Fixed

-   **Circular Dependency in Security Configuration:** Resolved startup failures by using `@Lazy` injection for `IUserService` and `IAuthService` in OAuth2 handlers.
-   **Frontend Environment Variables:** Corrected the use of environment variables in Vue from `process.env.VUE_APP_*` to `import.meta.env.VITE_*` to align with the Vite build tool.
-   **Thymeleaf Template Not Found:** Corrected file paths and ensured email HTML templates are correctly located and included in the build.
-   **Navbar Reactivity on Login:** Refactored the Navbar component to robustly fetch user data after login, ensuring admin links and profile images appear without a hard refresh.

## [v0.1.0-beta] - 2025-05-01

### Refactor

-   Initial project release with core features:
-   Core entities for User, Car, Booking, Rental.
-   JWT-based authentication and authorization.
-   Basic CRUD operations for all core entities.
-   Admin dashboard shell and user management UI.
-   File storage service abstraction with Local and MinIO implementations.
-   Initial Docker Compose setup for backend and database.