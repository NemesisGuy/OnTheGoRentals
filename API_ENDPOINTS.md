# API Endpoints Documentation

Base URL for all v1 APIs: `/api/v1`

---

## Authentication (`/auth`)
*(This section remains the same as previously defined)*
*   `POST /auth/register` (Public) - Req: `RegisterDto`, Resp: `AuthResponseDto` & `rtk` cookie
*   `POST /auth/login` (Public) - Req: `LoginDto`, Resp: `AuthResponseDto` & `rtk` cookie
*   `POST /auth/refresh` (Public, needs `rtk` cookie) - Resp: `TokenRefreshResponseDto` & new `rtk` cookie
*   `POST /auth/logout` (Authenticated) - Resp: Success message & clears `rtk` cookie

---

## Current User (`/users/me`)
*(This section remains the same as previously defined - All endpoints require authentication)*
*   `GET /users/me/profile` - Resp: `UserResponseDTO`
*   `PUT /users/me/profile` - Req: `UserUpdateRequestDTO`, Resp: `UserResponseDTO`
*   `GET /users/me/rental-history` - Resp: `List<RentalResponseDTO>` or `204 No Content`

---

## Public Car Information (`/cars`)
*(This section remains the same as previously defined - All endpoints are Public)*
*   `GET /cars` - Resp: `List<CarResponseDTO>`
*   `GET /cars/price-group/{group}` - Path Var: `group` (PriceGroup enum), Resp: `List<CarResponseDTO>`
*   `GET /cars/available` - Resp: `List<CarResponseDTO>`
*   `GET /cars/available/price-group/{group}` - Path Var: `group`, Resp: `List<CarResponseDTO>`
*   `GET /cars/{carUuid}` - Path Var: `carUuid` (UUID), Resp: `CarResponseDTO` or `404`

---

## Public Booking Operations (`/bookings`)
*(This section reflects the refactored user-facing BookingController - Most endpoints require authentication)*
*   `POST /bookings` - Req: `BookingRequestDTO`, Resp: `201` with `BookingResponseDTO`
*   `GET /bookings/my-bookings` - Resp: `List<BookingResponseDTO>` or `204`
*   `GET /bookings/{bookingUuid}` - Resp: `BookingResponseDTO` or `404`
*   `PUT /bookings/{bookingUuid}` - Req: `BookingUpdateDTO` (or `BookingRequestDTO` if reused), Resp: `BookingResponseDTO` or `404`
*   `POST /bookings/{bookingUuid}/confirm` - Resp: `BookingResponseDTO` or `404`
*   `POST /bookings/{bookingUuid}/cancel` - Resp: `BookingResponseDTO` or `404`
*   `GET /bookings/available-cars` (Helper) - Resp: `List<CarResponseDTO>` (Public or Authenticated)

---

## Public FAQs (`/faqs`)
*(All GET endpoints are Public)*
*   `GET /faqs` - Query Param: `category` (optional String), Resp: `List<FaqResponseDTO>` or `204`
*   `GET /faqs/{faqUuid}` - Resp: `FaqResponseDTO` or `404`

---

## Public Help Topics (`/help-topics`)
*(All GET endpoints are Public)*
*   `GET /help-topics` - Query Param: `category` (optional String), Resp: `List<HelpCenterResponseDTO>` or `204`
*   `GET /help-topics/{topicUuid}` - Resp: `HelpCenterResponseDTO` or `404`

---

## Public Feedback Submission (`/feedback`)
*   `POST /feedback` - Req: `FeedbackCreateDTO`, Resp: `201` with `FeedbackResponseDTO` (Public)

---

## Public Contact Us Submission (`/contact-us`)
*   `POST /contact-us` - Req: `ContactUsCreateDTO`, Resp: `201` with `ContactUsResponseDTO` (Public)

---
---

## Administrative Endpoints (`/admin`)

All endpoints under `/api/v1/admin` require ADMIN or SUPERADMIN authentication.

### Admin - User Management (`/admin/users`)
*   **`GET /admin/users`**
    *   Description: Retrieves a list of all users (admin view, may include soft-deleted).
    *   Response: `200 OK` with `List<UserResponseDTO>`. `204 No Content` if empty.
*   **`POST /admin/users`**
    *   Description: Creates a new user with specified roles and password.
    *   Request Body: `UserCreateDTO` (`firstName`, `lastName`, `email`, `password` (plain), `roleNames` (list of strings), optional `authProvider`, `googleId`, `profileImageUrl`).
    *   Response: `201 Created` with `UserResponseDTO`.
*   **`GET /admin/users/{userUuid}`**
    *   Description: Retrieves a specific user by their UUID.
    *   Path Variable: `{userUuid}` (UUID).
    *   Response: `200 OK` with `UserResponseDTO`. `404 Not Found`.
*   **`PUT /admin/users/{userUuid}`**
    *   Description: Updates an existing user's details, password, and/or roles.
    *   Path Variable: `{userUuid}` (UUID).
    *   Request Body: `UserUpdateDTO` (optional fields: `firstName`, `lastName`, `email`, `password` (plain), `roleNames`, `authProvider`, `googleId`, `profileImageUrl`, `deleted` flag).
    *   Response: `200 OK` with `UserResponseDTO`. `404 Not Found`.
*   **`DELETE /admin/users/{userUuid}`**
    *   Description: Soft-deletes a user by their UUID.
    *   Path Variable: `{userUuid}` (UUID).
    *   Response: `204 No Content`. `404 Not Found`.
*   **`GET /admin/users/roles`**
    *   Description: Lists all available roles in the system.
    *   Response: `200 OK` with `List<Role>` (entity). *Consider `RoleResponseDTO` for consistency.*

### Admin - Car Management (`/admin/cars`)
*   **`GET /admin/cars`** - Resp: `List<CarResponseDTO>` or `204`.
*   **`POST /admin/cars`** - Req: `CarCreateDTO`, Resp: `201` with `CarResponseDTO`.
*   **`GET /admin/cars/{carUuid}`** - Resp: `CarResponseDTO` or `404`.
*   **`PUT /admin/cars/{carUuid}`** - Req: `CarUpdateDTO`, Resp: `CarResponseDTO` or `404`.
*   **`DELETE /admin/cars/{carUuid}`** - Resp: `204 No Content` or `404`.

### Admin - Rental/Booking Management (`/admin/rentals`)
*   **`GET /admin/rentals`** - Resp: `List<RentalResponseDTO>` or `204`.
*   **`POST /admin/rentals`** - Req: `BookingRequestDTO` (or `AdminRentalCreateDTO`), Resp: `201` with `RentalResponseDTO`.
*   **`GET /admin/rentals/{rentalUuid}`** - Resp: `RentalResponseDTO` or `404`.
*   **`PUT /admin/rentals/{rentalUuid}`** - Req: `BookingUpdateDTO` (or `AdminRentalUpdateDTO`), Resp: `RentalResponseDTO` or `404`.
*   **`DELETE /admin/rentals/{rentalUuid}`** - Resp: `204 No Content` or `404`.
*   **`POST /admin/rentals/{rentalUuid}/confirm`** - Resp: `RentalResponseDTO` or `404`.
*   **`POST /admin/rentals/{rentalUuid}/cancel`** - Resp: `RentalResponseDTO` or `404`.
*   **`POST /admin/rentals/{rentalUuid}/complete`** - Query Param: `fineAmount` (double, optional), Resp: `RentalResponseDTO` or `404`.

### Admin - FAQ Management (`/admin/faqs`)
*   **`POST /admin/faqs`** (was `POST /faqs` in public, assuming admin now) - Req: `FaqCreateDTO`, Resp: `201` with `FaqResponseDTO`.
*   **`PUT /admin/faqs/{faqUuid}`** (was `PUT /faqs/{uuid}` in public) - Req: `FaqUpdateDTO`, Resp: `FaqResponseDTO` or `404`.
*   **`DELETE /admin/faqs/{faqUuid}`** (was `DELETE /faqs/{uuid}` in public) - Resp: `204 No Content` or `404`.
*   **Note:** Public GETs for FAQs are at `/api/v1/faqs`. Admin might have a separate GET `/api/v1/admin/faqs` if it shows different data (e.g., including deleted). If it's the same data, admin just uses the public one. For simplicity, I've assumed admin CRUD is distinct.

### Admin - Help Topic Management (`/admin/help-topics`)
*   **`POST /admin/help-topics`** - Req: `HelpCenterCreateDTO`, Resp: `201` with `HelpCenterResponseDTO`.
*   **`PUT /admin/help-topics/{topicUuid}`** - Req: `HelpCenterUpdateDTO`, Resp: `HelpCenterResponseDTO` or `404`.
*   **`DELETE /admin/help-topics/{topicUuid}`** - Resp: `204 No Content` or `404`.
*   **Note:** Public GETs for Help Topics are at `/api/v1/help-topics`.

### Admin - Feedback Management (`/admin/feedback`)
*   **`GET /admin/feedback`** (was public `GET /feedback`) - Resp: `List<FeedbackResponseDTO>` or `204`.
*   **`GET /admin/feedback/{feedbackUuid}`** (was public `GET /feedback/{uuid}`) - Resp: `FeedbackResponseDTO` or `404`.
*   **`DELETE /admin/feedback/{feedbackUuid}`** (was public `DELETE /feedback/{uuid}`) - Resp: `204 No Content` or `404`.
*   **Note:** Public POST for Feedback is at `/api/v1/feedback`.

### Admin - Contact Us Submissions Management (`/admin/contact-us-submissions`)
*   **`GET /admin/contact-us-submissions`** - Resp: `List<ContactUsResponseDTO>` or `204`.
*   **`GET /admin/contact-us-submissions/{submissionUuid}`** - Resp: `ContactUsResponseDTO` or `404`.
*   **`PUT /admin/contact-us-submissions/{submissionUuid}`** - Req: `AdminContactUsUpdateDTO`, Resp: `ContactUsResponseDTO` or `404`.
*   **`DELETE /admin/contact-us-submissions/{submissionUuid}`** - Resp: `204 No Content` or `404`.
*   **Note:** Public POST for Contact Us is at `/api/v1/contact-us`.

### Admin - About Us Content Management (`/admin/about-us`)
*   **`POST /admin/about-us`** - Req: `AboutUsCreateDTO`, Resp: `201` with `AboutUsResponseDTO`. (If you allow multiple, otherwise PUT to a singleton resource).
*   **`GET /admin/about-us/{aboutUsUuid}`** - Resp: `AboutUsResponseDTO` or `404`.
*   **`PUT /admin/about-us/{aboutUsUuid}`** - Req: `AboutUsUpdateDTO`, Resp: `AboutUsResponseDTO` or `404`.
*   **`GET /admin/about-us`** - Resp: `List<AboutUsResponseDTO>` or `204`.
*   **`DELETE /admin/about-us/{aboutUsUuid}`** - Resp: `204 No Content` or `404`.
*   **Note:** Public GET for About Us might be `/api/v1/about-us/latest` or similar.

---

This documentation provides a good overview. You'll want to:
*   Fill in the details for any controllers we didn't explicitly refactor in the last few steps (e.g., `AboutUs` for public view if it exists).
*   Ensure the "Authentication" column is accurate based on your `SpringSecurityConfig`.
*   Add more details to the "Description" for each endpoint as needed.
*   Double-check that all request/response DTO names match what's in your code.

