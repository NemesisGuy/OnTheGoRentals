# API Endpoints Documentation

Base URL for all v1 APIs: `/api/v1`

---

## Authentication (`/auth`)
*(This section remains the same as previously defined)*

*   **`POST /auth/register`**
    *   Request: `RegisterDto
*   `POST /auth/login` (Public) - Req: `LoginDto`, Resp: `AuthResponseDto` & `rtk` cookie
*   `POST /auth/refresh` (Public, needs `rtk` cookie) - Resp: `TokenRefreshResponseDto` & new `rtk` cookie
*   `POST /auth/logout` (Authenticated) - Resp: Success message & clears `rtk` cookie

---

## Current User (`/users/me`)
*(All endpoints require authentication)*
*   `GET /users/me/profile` - Resp: `UserResponseDTO`
*   `PUT /users/me/profile` - Req: `UserUpdateRequest` | Response: `AuthResponseDto` (Access Token in body, Refresh Token in HttpOnly cookie)
*   **`POST /auth/login`**
    *   Request: `LoginDto` | Response: `AuthResponseDto` (Access Token in body, Refresh Token in HttpOnly cookie)
*   **`POST /auth/refresh`**
    *   Request Cookie: `rtk` | Response: `TokenRefreshResponseDto` (New Access Token in body, New Refresh Token in HttpOnly cookie)
*   **`POST /auth/logout`**
    DTO`, Resp: `UserResponseDTO`
*   `GET /users/me/rental-history` - Resp: `List<RentalResponseDTO>` or `204 No Content`

---

## Rentals (`/rent    *   Response: `200 OK` (Clears HttpOnly refresh token cookie)
    *   Authentication: Required.

---

## Current User (`/users/me`)
*(This section remains the same as previously defined)*

*   **`GET /users/me/profile`**
    *   Response: `UserResponseDTO`
    *   Authentication: Required.
*   **`PUT /users/me/profile`**
    als`)
    *(Most endpoints require authentication, specific public helpers noted)*

*   **`POST /rentals`**
    *   Description: Creates a new rental for the currently authenticated user.
    *   Request Body: `RentalRequestDTO` (`carUuid`, `driverUuid` (optional), `issuedDate`, `expectedReturnedDate`)
    *   Response: `201 Created` with `RentalResponseDTO`.
    *   Authentication: Required.

*   **`GET /rentals/my-rentals`**
    *   Description: Retrieves all rentals for the currently authenticated user.
    *   Response: `200 OK` with `List<RentalResponseDTO>`. `204 No Content` if none.
    *   Authentication: Required.

*    *   Request: `UserUpdateRequestDTO` | Response: `UserResponseDTO`
         *   Authentication: Required.
*   **`GET /users/me/rental-history`**
    *   Response: `List<RentalResponseDTO>`
    *   Authentication: Required.

---

## Bookings (`/bookings`)
*(Updated   **`GET /rentals/{rentalUuid}`**
*   Description: Retrieves a specific rental by its UUID.
*   Path Variable: `{rentalUuid}` (UUID of the rental).
*   Response: `200 OK` with `RentalResponseDTO`. `404 Not Found`.
*   Authentication: Required (user should only see their own, or admin all - requires service-level authorization).

*   **`PUT /rentals/{rentalUuid}`**
    *   Description: Updates an existing rental (e.g., `expectedReturnedDate`).
    *   Path Variable: `{rentalUuid}` (UUID of the rental).
    *   Request Body: based on latest refactor)*

*   **`POST /bookings`**
    *   Description: Creates a new booking for the currently authenticated user.
    *   Request Body: `BookingRequestDTO` (`carUuid`, `driverUuid` (optional), `issuedDate`, `expectedReturnedDate`). User is inferred from auth.
    *   Response: `201 Created` with `BookingResponseDTO`.
    *   Authentication: Required.

*   **`GET /bookings/my-bookings`**
    *   Description: Retrieves all bookings for the currently authenticated user.
    *   Response: `200 OK` with `List<BookingResponseDTO>`. `204 No Content` if none.
    *   Authentication: Required.

*   **`GET /bookings/{bookingUuid}`**
    *   Description: Retrieves a specific booking by its UUID.
    *   Path Variable: `{bookingUuid}` (UUID of the booking).
    *   Response: `200 OK` with `BookingResponseDTO `RentalUpdateDTO` (contains fields that can be updated).
    *   Response: `200 OK` with `RentalResponseDTO`. `404 Not Found`.
    *   Authentication: Required (user for own, or admin).

*   **`POST /rentals/{rentalUuid}/confirm`**
    *   Description: Confirms a pending rental.
    *   Path Variable: `{rentalUuid}`.
    *   Response: `200 OK` with `RentalResponseDTO`. `404 Not Found`.
    *   Authentication: Required (typically user for own, or admin).

*   **`POST /rentals/{rentalUuid}/cancel`**
    *   Description: Cancels a rental.
    *   Path Variable: `{rentalUuid}`.
    *   Response: `200 OK` with `RentalResponseDTO`. `404`. `404 Not Found`.
    *   Authentication: Required (Authorization logic in service should ensure user owns it or is admin).

*   **`PUT /bookings/{bookingUuid}`**
    *   Description: Updates an existing booking (e.g., dates, if rules allow).
    *   Path Variable: `{bookingUuid}` (UUID of the booking).
    *   Request Body: `RentalUpdateDTO` (or a more specific `BookingUpdateDTO`).
    *   Response: `200 OK` with `BookingResponseDTO`. `404 Not Found`.
    * Not Found`.
    *   Authentication: Required (user for own, or admin).

*   **`POST /rentals/{rentalUuid}/complete`**
    *   Description: Marks a rental as completed and records any fines. (Typically Admin/Staff)
    *   Path Variable: `{rentalUuid}`.
    *   Query Parameter: `fineAmount` (double, optional, defaults to 0.0).
    *   Response: `200 OK` with `RentalResponseDTO`. `404 Not Found`.
    *   Authentication: Required (Admin/Staff).

*   **`GET /rentals/available-cars`** (Helper Endpoint)
    *   Description: Retrieves a list of cars currently available for booking.
    *   Response: `2   Authentication: Required (Authorization logic for ownership/admin).

*   **`POST /bookings/{bookingUuid}/confirm`**
    *   Description: Confirms a pending booking.
    *   Path Variable: `{bookingUuid}`.
    *   Response: `200 OK` with `BookingResponseDTO`. `404 Not Found`.
    *   Authentication: Required (Admin or specific user states).

*   **`POST /bookings/{bookingUuid}/cancel`**
    *   Description: Cancels a booking.
    *   Path Variable: `{bookingUuid}`.
    *   Response: `200 OK` with `BookingResponseDTO`. `404 Not Found00 OK` with `List<CarResponseDTO>`. `204 No Content` if none.
    *   Authentication: Public (or Authenticated, as per your `SpringSecurityConfig`).

---

## Public Car Information (`/cars`)
*(All endpoints are Public)*
*   `GET /cars` (or `/cars/list/all`) - Resp: `List<CarResponseDTO>`
*   `GET /cars/price-group/{group}` - Path Var: `group` (PriceGroup enum), Resp: `List<CarResponseDTO>`
*   `GET /cars/available` (or `/cars/list/available/all`) - Resp: `List<CarResponseDTO>`
*   `GET /cars/available/price-group/{group}` - Path Var: `group`.
    *   Authentication: Required (User for own, or Admin).

*   **`POST /bookings/{bookingUuid}/complete`**
    *   Description: Marks a rental as completed (e.g., car returned).
    *   Path Variable: `{bookingUuid}`.
    *   Query Parameter: `fineAmount` (optional `double`, defaults to 0.0).
    *   Response: `200 OK` with `BookingResponseDTO`.
    *   Authentication: Required (Typically Admin/Staff).

*   **`GET /bookings/available-cars`** (Helper Endpoint)
    *   Description: Retrieves a list of cars currently available for booking.
    *   Response: `200 OK`, Resp: `List<CarResponseDTO>`
*   `GET /cars/{carId}` - Path Var: `carId` (UUID), Resp: `CarResponseDTO` or `404`

---

## Help Topics (`/help-topics`)
*(GET endpoints are Public, others typically Admin)*

*   **`GET /help-topics`**
    *   Description: Retrieves all non-deleted help topics. Can be filtered by category.
    *   Query Parameter: `category` (String, optional).
    *   Response: `200 OK` with `List<HelpCenterResponseDTO>`. `204 No Content` if none.
    *   Authentication` with `List<CarResponseDTO>`. `204 No Content` if none.
    *   Authentication: Public or Required (Your config has it as `permitAll()`).

---

## Public Car Information (`/cars`)
*(This section remains the same as previously defined)*

*   **`GET /cars`** (Replaces `/cars/list/all`)
    *   Response: `List<CarResponseDTO>`.
*   **`GET /cars/price-group/{group}`**
    *   Response: `List<CarResponseDTO>`.
*   **`GET /cars/available`** (Replaces `/cars/list/available/all`)
    *   Response: `List<CarResponseDTO>`.
*   **`GET /cars/available/: Public.

*   **`GET /help-topics/{topicUuid}`**
    *   Description: Retrieves a specific help topic by its UUID.
    *   Path Variable: `{topicUuid}` (UUID).
    *   Response: `200 OK` with `HelpCenterResponseDTO`. `404 Not Found`.
    *   Authentication: Public.

*   **`POST /help-topics`** (Admin)
    *   Description: Creates a new help topic.
    *   Request Body: `HelpCenterCreateDTO` (`title`, `content`, `category`).
    *   Response: `201 Created` with `HelpCenterResponseDTO`.
    *   Authentication: Admin.

*   **`PUT /help-topics/{topicUuid}`** (Admin)
    *   Description: Updates an existing help topic.
    *   Path Variable: `{topicUuidprice-group/{group}`**
    *   Response: `List<CarResponseDTO>`.
*   **`GET /cars/{carUuid}`** (Path variable changed from `carId` for consistency if not already)
    *   Response: `CarResponseDTO`.

---

## Admin Car Management (`/admin/cars`)
*(This section remains the same as previously defined, ensuring UUIDs are used in paths)*

*   **`GET /admin/cars`** (Replaces `/admin/cars/all`)
    *   Response: `List<CarResponseDTO>`.
*   **`POST /admin/cars`** (Replaces `/admin/cars/create`)
    *   Request: `CarCreateDTO` | Response: `CarResponseDTO` (`201 Created`).
*   **`GET /admin/cars/{carUuid}`** (Replaces `/admin/cars/read/{carUuid}`)
    *   Response: `CarResponseDTO`.
*   **`PUT /admin/cars/{carUuid}`** (Replaces `/admin/cars/update/{carUuid}`)
    *   Request: `CarUpdateDTO` | Response: `CarResponseDTO`.
*   **`DELETE /admin/cars/{}` (UUID).
    *   Request Body: `HelpCenterUpdateDTO` (optional `title`, `content`, `category`).
    *   Response: `200 OK` with `HelpCenterResponseDTO`. `404 Not Found`.
    *   Authentication: Admin.

*   **`DELETE /help-topics/{topicUuid}`** (Admin)
    *   Description: Soft-deletes a help topic.
    *   Path Variable: `{topicUuid}` (UUID).
    *   Response: `204 No Content`. `404 Not Found`.
    *   Authentication: Admin.

---

## Feedback (`/feedback`)
*(POST is Public, GET/DELETE typically Admin)*

*   **`POST /feedback`**
    *   Description: Submits new feedback.
    *   Request Body: `FeedbackCreateDTO` (`name`, `comment`).
    *   Response: `201 Created` with `FeedbackResponseDTO`.
    *   Authentication: Public.

*   **`GET /feedback`** (Admin)
    *   Description: Retrieves all non-deleted feedback submissions.
    *   Response: `200 OK` with `List<FeedbackResponseDTO>`. `2carUuid}`** (Replaces `/admin/cars/delete/{carUuid}`)
    *   Response: `204 No Content`.

---

## Contact Us Submissions (`/contact-us`)

*   **`POST /contact-us`**
    *   Description: Submits a new contact form entry.
    *   Request Body: `ContactUsCreateDTO` (`title` (optional), `firstName`, `lastName`, `email`, `subject`, `message`).
    *   Response: `201 Created` with `ContactUsResponseDTO`.
    *   Authentication: Public.
    *   *(Admin endpoints for viewing/managing these would be under `/admin/contact-us`)*

---

## FAQs (`/faqs`) - Frequently Asked Questions

*   **`GET04 No Content` if none.
    *   Authentication: Admin.

*   **`GET /feedback/{feedbackUuid}`** (Admin)
    *   Description: Retrieves a specific feedback entry by UUID.
    *   Path Variable: `{feedbackUuid}` (UUID).
    *   Response: `200 OK` with `FeedbackResponseDTO`. `404 Not Found`.
    *   Authentication: Admin.

*   **`DELETE /feedback/{feedbackUuid}`** (Admin)
    *   Description: Soft-deletes a feedback entry.
    *   Path Variable: `{feedbackUuid}` (UUID).
    *   Response: `204 No Content`. `404 Not Found`.
    *   Authentication: Admin.

---

## Admin Car Management (`/admin/cars`)
*(All endpoints require ADMIN/SUPERADMIN role)*
*   `GET /admin/cars/all` - Resp: `List<CarResponseDTO>`
*   `POST /admin/cars/create` - Req: `CarCreateDTO`, Resp: `201` with `CarResponseDTO`
*   `GET /admin/cars/ /faqs`**
    *   Description: Retrieves all non-deleted FAQs. Can be filtered by category.
    *   Query Parameter: `category` (optional string).
    *   Response: `200 OK` with `List<FaqResponseDTO>`. `204 No Content` if none.
    *   Authentication: Public.

*   **`GET /faqs/{faqUuid}`**
    *   Description: Retrieves a specific FAQ by its UUID.
    *   Path Variable: `{faqUuid}` (UUID of the FAQ).
    *   Response: `200 OK` with `FaqResponseDTO`. `404 Not Found`.
    *   Authentication: Public.

*   **Admin FAQ Management (Typically under `/admin/faqs` but shown here if combined for simplicity):**
    *   **`POST /faqs`** (Needs to be differentiatedread/{carUuid}` - Path Var: `carUuid`, Resp: `CarResponseDTO` or `404`
*   `PUT /admin/cars/update/{carUuid}` - Path Var: `carUuid`, Req: `CarUpdateDTO`, Resp: `CarResponseDTO` or `404`
*   `DELETE /admin/cars/delete/{carUuid}` - Path Var: `carUuid`, Resp: `204` or `404`

---

## Admin User Management (`/admin/users`) - Assuming this exists
*(All endpoints require ADMIN/SUPERADMIN role)*
*   `GET /admin/users/list/all` - Resp: `List<UserResponseDTO>`
*   `POST /admin/users/create` - Req: `UserCreateDTO` (needs password, roles), Resp: `UserResponseDTO`
*   `GET /admin/users/read/{userUuid}` - Resp: `UserResponseDTO`
*   `PUT /admin/users/update/{userUuid}` - Req: `UserUpdateDTO` (might include roles, ability to change password if admin), Resp: `UserResponseDTO from public GET if not an admin path, or move to `/admin/faqs`)
        *   Description: Creates a new FAQ.
        *   Request Body: `FaqCreateDTO`.
        *   Response: `201 Created` with `FaqResponseDTO`.
    *   Authentication: Admin.
    *   **`PUT /faqs/{faqUuid}`**
        *   Description: Updates an existing FAQ.
        *   Path Variable: `{faqUuid}`.
        *   Request Body: `FaqUpdateDTO`.
        *   Response: `200 OK` with `FaqResponseDTO`.
        *   Authentication: Admin.
    *   **`DELETE /faqs/{faqUuid}`**
        *   Description: Soft-deletes an FAQ.
        *   Path Variable: `{faqUuid}`.
        *   Response: `204 No Content`.
        *   Authentication: Admin.

---

## Feedback Submissions (`/feedback`)

*   **`POST /feedback`**
    *   Description: Submits new feedback.
    *   Request Body: `FeedbackCreateDTO` (`name`, `comment`).
    *   Response: `201 Created` with `FeedbackResponseDTO`.`
*   `DELETE /admin/users/delete/{userUuid}` - Resp: `204 No Content`

---
*(Continue for other controllers like `AboutUsController`, `ContactUsController` if they were also refactored, following the same pattern: clear resource path, standard HTTP methods, DTOs for request/response, UUIDs for external IDs.)*