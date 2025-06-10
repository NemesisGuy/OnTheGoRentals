# OnTheGoRentals

[![Java Version](https://img.shields.io/badge/Java-21-yellow.svg?style=for-the-badge)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot Version](https://img.shields.io/badge/Spring_Boot-3.5.0-blueviolet.svg?style=for-the-badge)](https://spring.io/projects/spring-boot)
[![Spring Security Version](https://img.shields.io/badge/Spring_Security-6.5.0-blueviolet.svg?style=for-the-badge)](https://spring.io/projects/spring-security)
[![JWT Authentication](https://img.shields.io/badge/Authentication-JWT-blue.svg?style=for-the-badge)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg?style=for-the-badge)](docs/LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg?style=for-the-badge)](https://example.com/build-status) <!-- Placeholder -->
[![Docker Version](https://img.shields.io/badge/Docker-Ready-blue.svg?style=for-the-badge)](https://www.docker.com/)

## Project Description

OnTheGoRentals is a full-stack car rental application designed to provide a seamless experience for users looking to rent vehicles and a comprehensive management platform for administrators. The system features a backend API built with Java Spring Boot and a separate Single Page Application (SPA) frontend developed with Vue.js.

The core purpose is to allow registered users to browse available cars, make bookings, and manage their rental history. Administrators have access to a dedicated dashboard to oversee users, vehicles, rental transactions, site content like FAQs, and more.

## Key Features

*   **User Management:** Secure user registration, login/logout functionality, and profile management.
*   **JWT-Based Authentication:** Robust and stateless authentication using JSON Web Tokens, typically involving short-lived access tokens and refresh tokens managed via cookies.
*   **Car Fleet Management:** Admins can add, update, and remove car details, including pricing and availability.
*   **Car Browsing & Search:** Users can browse the car catalog, filter by various criteria (e.g., price group), and view detailed information for each vehicle.
*   **Rental Booking System:** Users can book available cars for specific periods, view their current and past bookings, and manage them.
*   **Administrative Dashboard:**
    *   Manage Users (view, create, update, delete, assign roles).
    *   Manage Cars (CRUD operations).
    *   Manage Rentals (view all bookings, confirm, cancel, mark as complete).
    *   Content Management for FAQs, Help Topics, and About Us sections.
    *   View and manage user feedback and contact submissions.
*   **Public Information Endpoints:** API access for FAQs, Help Topics, submitting Feedback, and Contact Us forms.

## Technology Stack

*   **Backend:**
    *   Java 21
    *   Spring Boot 3.5.0
    *   Spring Security 6.5.0 (with JWT for authentication)
    *   Spring Data JPA (for database interaction)
    *   MySQL (Database)
    *   Maven (Build Tool)
*   **Frontend:**
    *   Vue.js (Developed and maintained in a separate repository: [OnTheGoRentalsFrontend](https://github.com/NemesisGuy/OnTheGoRentalsFrontend))
*   **API:**
    *   RESTful API architecture
    *   Base Path: `/api/v1`
    *   Secured using JSON Web Tokens (JWT)
*   **Containerization:**
    *   Docker
    *   Docker Compose

For a more detailed architectural overview, please see our [System Overview Document](docs/SystemOverview.md).

## Getting Started

To get the OnTheGoRentals system up and running, follow these instructions.

### Prerequisites

*   **Java Development Kit (JDK):** Version 21 or later.
*   **Maven:** Apache Maven build tool.
*   **Docker:** Docker Desktop or Docker Engine with Docker Compose.
*   **Node.js & npm/yarn:** Required if you plan to build and run the [frontend application](https://github.com/NemesisGuy/OnTheGoRentalsFrontend) locally.
*   **Git:** For cloning the repository.

### Backend Setup & Running (Docker - Recommended)

This is the easiest way to get the backend server running along with a MySQL database.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/NemesisGuy/OnTheGoRentals.git
    cd OnTheGoRentals
    ```

2.  **Configure Environment Variables:**
    The application uses environment variables for configuration. For local Docker Compose setups, create a `.env` file in the project root directory (where `docker-compose.yaml` is located).
    Refer to `docker/docker-compose-stack-dploy.yaml` for a comprehensive list of available environment variables.
    At a minimum, your `.env` file should include:
    ```env
    # Database Configuration (ensure these match your MySQL service in docker-compose if you add one, or an external DB)
    # SPRING_DATASOURCE_URL=jdbc:mysql://your_mysql_host:your_mysql_port/OnTheGoRentalsDatabase?createDatabaseIfNotExist=true
    # SPRING_DATASOURCE_USERNAME=your_mysql_user
    # SPRING_DATASOURCE_PASSWORD=your_mysql_password

    # JWT Secret (generate a strong random string)
    # JWT_SECRET=your-super-strong-jwt-secret-key

    # Example for using the provided docker-compose.yaml which expects an external MySQL or one defined elsewhere:
    # If you add a MySQL service to the main docker-compose.yaml named 'mysql-db', the URL might be:
    # SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/OnTheGoRentalsDatabase?createDatabaseIfNotExist=true
    # SPRING_DATASOURCE_USERNAME=root
    # SPRING_DATASOURCE_PASSWORD=mysecretpassword
    # JWT_SECRET=replace_with_a_strong_secret_key_min_64_chars_long_for_HS512
    ```
    **Important:** The provided `docker/docker-compose.yaml` references a pre-built image `nemesisguy/on-the-go-rentals-backend:latest` and expects the database `mysql-container` to be available on the `app-network`. You might need to adjust this or add a MySQL service definition to your `docker-compose.yaml` for a complete local stack.

3.  **Run with Docker Compose:**
    If you have a complete `docker-compose.yaml` (including a MySQL service if not using an external one):
    ```bash
    docker-compose up -d
    ```
    If you are using the provided `docker/docker-compose.yaml` which assumes an external MySQL and pre-built image:
    Ensure your `.env` file correctly points to your MySQL instance and defines other necessary variables like `JWT_SECRET`.
    ```bash
    # (Assuming you are in the project root)
    docker-compose -f docker/docker-compose.yaml up -d 
    ```
    *(You might need to run `docker network create app-network` if it doesn't exist)*


4.  The backend API should now be accessible at `http://localhost:8080`.

### Backend Setup & Running (Local/Manual)

1.  **Clone the repository:** (If not already done)
    ```bash
    git clone https://github.com/NemesisGuy/OnTheGoRentals.git
    cd OnTheGoRentals
    ```

2.  **Configure `application.properties`:**
    Located in `src/main/resources/application.properties`.
    You'll need to set up your database connection details (URL, username, password) and JWT secret.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/OnTheGoRentalsDatabase?createDatabaseIfNotExist=true&useSSL=false
    spring.datasource.username=your_db_user
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=update # Or 'validate' for production after initial setup

    # JWT Configuration
    jwt.secret=your-super-strong-jwt-secret-key # Ensure this is a very strong key
    jwt.accesstoken.expiration=3600000 # 1 hour in milliseconds
    jwt.refreshtoken.expiration=604800000 # 7 days in milliseconds
    ```

3.  **Build and Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The backend API should now be accessible at `http://localhost:8080`.

### Frontend Setup

For instructions on setting up and running the Vue.js frontend application, please refer to the README file in the [OnTheGoRentalsFrontend repository](https://github.com/NemesisGuy/OnTheGoRentalsFrontend).

## Usage

Once the backend (and frontend) are running:
*   **Users** can register for an account, log in, browse available cars, make bookings, and view their rental history.
*   **Administrators** can log in with admin credentials to access the admin dashboard for managing users, cars, rentals, and site content.
    *   Default user accounts (e.g., `user@gmail.com`, `admin@gmail.com`, `superadmin@gmail.com`) are typically created via data seeding during application startup if they don't already exist. The default password pattern before hashing is `rolename + "password"` (e.g., 'adminpassword'). These passwords will be hashed by the system. Please refer to `DefaultDataInitializer.java` for the exact logic.

For detailed information on API endpoints, request/response formats, and authentication mechanisms, please refer to our [API Endpoints Documentation](docs/API_ENDPOINTS.md).

## Running Tests (Backend)

To run the backend unit and integration tests, use the following Maven command:

```bash
mvn test
```

## Contributing

We welcome contributions to OnTheGoRentals! Whether it's bug reports, feature suggestions, or code improvements, please read our [Contributing Guidelines](docs/CONTRIBUTING.md) to get started.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](docs/LICENSE) file for details.

## Lead Author & Maintainer

This project is primarily maintained by:

- **Peter Buckingham (Team Lead)** - [NemesisGuy](https://github.com/NemesisGuy)

Past contributions to the project are acknowledged. For a list of past contributors, please see [PAST_CONTRIBUTORS.md](docs/PAST_CONTRIBUTORS.md).

## Contact / Support

For support requests, questions, or to report issues, please use the **GitHub Issues** section of this repository.
