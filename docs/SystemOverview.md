# System Overview: OnTheGoRentals

## 1. Introduction

OnTheGoRentals is a comprehensive car rental system designed to facilitate the process of renting vehicles for users and provide administrators with tools to manage the platform. It covers functionalities from user registration and vehicle browsing to booking management and administrative oversight of the entire system.

## 2. System Components

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

## 3. Technology Stack Summary

*   **Backend:** Java 21, Spring Boot 3.5.0, Spring Security 6.5.0, Spring Data JPA, JWT (JSON Web Tokens)
*   **Frontend:** Vue.js
*   **Database:** MySQL
*   **Build Tool:** Maven
*   **Containerization:** Docker, Docker Compose

## 4. Architecture Overview

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

## 5. Deployment

The OnTheGoRentals application is designed for deployment using **Docker containers**.
*   The backend application is packaged into a Docker image.
*   `docker-compose.yaml` files are provided to orchestrate the deployment of the application service, potentially alongside other services like the MySQL database, especially for local development and testing.
*   A `docker-compose-stack-dploy.yaml` file suggests readiness for deployment in a Docker Swarm environment for more scalable setups.

This overview provides a high-level understanding of the OnTheGoRentals system architecture and its core components. For more detailed information, refer to the specific documentation for the API endpoints and other project files.
