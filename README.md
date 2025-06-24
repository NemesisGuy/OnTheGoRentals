# On-The-Go Rentals

[![Latest Release](https://img.shields.io/github/v/release/NemesisGuy/OnTheGoRentals?include_prereleases&label=latest%20beta&color=blue&style=for-the-badge)](https://github.com/NemesisGuy/OnTheGoRentals/releases/latest)
[![Java Version](https://img.shields.io/badge/Java-21-yellow.svg?style=for-the-badge)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot Version](https://img.shields.io/badge/Spring_Boot-3.5.0-blueviolet.svg?style=for-the-badge)](https://spring.io/projects/spring-boot)
[![Spring Security Version](https://img.shields.io/badge/Spring_Security-6.5.0-blueviolet.svg?style=for-the-badge)](https://spring.io/projects/spring-security)
[![JWT Authentication](https://img.shields.io/badge/Authentication-JWT-blue.svg?style=for-the-badge)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg?style=for-the-badge)](docs/LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg?style=for-the-badge)](https://github.com/NemesisGuy/OnTheGoRentals/actions) <!-- Placeholder for GitHub Actions -->
[![Docker Version](https://img.shields.io/badge/Docker-Ready-blue.svg?style=for-the-badge)](https://www.docker.com/)

## Project Description

OnTheGoRentals is a full-stack car rental application designed to provide a seamless experience for users looking to rent vehicles and a comprehensive management platform for administrators. The system features a backend API built with Java Spring Boot and a separate Single Page Application (SPA) frontend developed with Vue.js.

The core purpose is to allow registered users to browse available cars, make bookings, and manage their rental history. Administrators have access to a dedicated dashboard to oversee users, vehicles, rental transactions, site content, and application metrics.

## Key Features

*   **User Management:** Secure user registration, login/logout, and profile management.
*   **Advanced Authentication:**
    *   **JWT-Based Security:** Robust authentication using short-lived access tokens and securely-managed refresh tokens (via HttpOnly cookies).
    *   **Google OAuth2 Login:** Users can register and sign in seamlessly with their Google accounts.
    *   **Password Reset:** A secure "Forgot Password" flow that uses email verification.
*   **Car Fleet Management:** Admins can add, update, and remove car details, including images, pricing, and availability.
*   **Car Browsing & Search:** Users can browse the car catalog and view detailed information for each vehicle.
*   **Rental Booking System:** Users can book available cars, view their current and past bookings, and manage them.
*   **Administrative Dashboard:**
    *   Live analytics dashboard with key performance indicators (KPIs) and data visualizations.
    *   Manage Users (view, create, update, delete, assign roles).
    *   Manage Cars (CRUD operations).
    *   Manage Rentals and Bookings.
    *   Content Management for FAQs, Help Topics, and About Us sections.
*   **Transactional Emails:** The system sends automated HTML emails for critical events like user registration and password resets.
*   **Monitoring & Observability:** The Docker stack includes a full observability suite with Prometheus for metrics, Grafana for visualization, and Loki for centralized logging.

## Technology Stack

*   **Backend:**
    *   Java 21
    *   Spring Boot 3.5.0
    *   Spring Security 6.5.0 (JWT & OAuth2)
    *   Spring Data JPA / Hibernate
    *   **Thymeleaf:** For server-side HTML email templating.
    *   MySQL
    *   Maven
*   **Frontend:**
    *   Vue.js 3 (Composition API)
    *   Maintained in a separate repository: [OnTheGoRentalsFrontend](https://github.com/NemesisGuy/OnTheGoRentalsFrontend)
*   **API Documentation:**
    *   OpenAPI 3 via `springdoc-openapi`
*   **Containerization & DevOps:**
    *   Docker & Docker Compose
    *   **Prometheus:** For metrics collection.
    *   **Grafana:** For metrics visualization and dashboards.
    *   **Loki & Promtail:** For centralized log aggregation.

For a more detailed architectural overview, please see our [System Overview Document](docs/SystemOverview.md).

## Getting Started

To get the OnTheGoRentals system up and running, follow these instructions.

### Prerequisites

*   **Java Development Kit (JDK):** Version 21 or later.
*   **Maven:** Apache Maven build tool.
*   **Docker:** Docker Desktop or Docker Engine with Docker Compose.
*   **Node.js:** Required to run the [frontend application](https://github.com/NemesisGuy/OnTheGoRentalsFrontend).
*   **Git:** For cloning the repository.

### Backend Setup & Running (Local Development)

This method is recommended for active development and debugging within an IDE like IntelliJ IDEA.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/NemesisGuy/OnTheGoRentals.git
    cd OnTheGoRentals
    ```

2.  **Run Dependencies with Docker:** Before running the app locally, start the required services (database, MinIO, etc.) using Docker Compose. This ensures your local app has something to connect to.
    ```bash
    # From the project root
    docker-compose up -d mysql minio
    ```

3.  **Configure Local Secrets:**
    The project uses a `application-secrets.properties` file for local development, which is **not** committed to Git.
    *   In the `src/main/resources/` directory, create a new file named `application-secrets.properties`.
    *   Populate it with your local development secrets. This includes database credentials, JWT secrets, and your Google OAuth2 client details. Refer to the example below:

    <details>
      <summary>Click to see an example `application-secrets.properties` file</summary>

      ```properties
      # LOCAL DEVELOPMENT SECRETS (src/main/resources/application-secrets.properties)
      SERVER_PORT=8080
      
      # Database (connecting to Docker container on host)
      DB_URL=jdbc:mysql://localhost:3307/OnTheGoRentalsDatabase?createDatabaseIfNotExist=true&useSSL=false
      DB_USERNAME=root
      DB_PASSWORD=my_secret_password
      
      # JWT Secret (generate a strong random string)
      JWT_SECRET=your-super-strong-jwt-secret-key-that-is-very-long
      
      # Google OAuth2 Credentials
      GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
      GOOGLE_CLIENT_SECRET=your-google-client-secret
      
      # MinIO (connecting to Docker container on host)
      MINIO_URL=http://localhost:9012
      MINIO_ACCESS_KEY=minioadmin
      MINIO_SECRET_KEY=minioadmin
      
      # Email Relay (e.g., Brevo)
      SPRING_MAIL_USERNAME=your_brevo_login_email@example.com
      SPRING_MAIL_PASSWORD=your_brevo_smtp_key
      APP_EMAIL_FROM=your_verified_sender@yourdomain.com
      
      # Frontend URL for local dev
      APP_FRONTEND_URL=http://localhost:5173
      ```
    </details>

4.  **Run the Application:**
    You can now run the `BackendApplication` main method directly from your IDE (e.g., IntelliJ). The application will automatically pick up the configuration from both `application.properties` and your new `application-secrets.properties` file.

### Full Stack Deployment (Docker Compose)

The provided `docker-compose.yml` file will run the entire application stack, including the backend, database, and observability tools.

1.  **Configure Environment Variables:** Before deploying, ensure you have set up the necessary environment variables for your deployment environment (e.g., in Portainer's stack editor). Refer to the `environment` section in the `docker-compose.yml` file for a full list of required variables.

2.  **Run with Docker Compose:**
    ```bash
    docker-compose up -d
    ```

3.  The services will be available at their configured ports:
    *   **Backend API:** `http://localhost:8087`
    *   **Frontend UI:** `http://localhost:8081`
    *   **Grafana:** `http://localhost:3000`
    *   **Prometheus:** `http://localhost:9090`

### Frontend Setup

For instructions on setting up and running the Vue.js frontend, please refer to the README in the [OnTheGoRentalsFrontend repository](https://github.com/NemesisGuy/OnTheGoRentalsFrontend).

## API Documentation with Swagger

Once the application is running, you can access the interactive Swagger UI at:

`http://localhost:8080/swagger-ui.html` (for local dev)

This documentation allows you to explore all API endpoints, view models, and test endpoints directly from the browser.

## Contributing

We welcome contributions! Please read our [Contributing Guidelines](docs/CONTRIBUTING.md) to get started.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](docs/LICENSE) file for details.