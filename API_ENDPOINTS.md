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

# API Endpoints Documentation

Base URL for all v1 APIs: `/api/v1`

---
*(Sections for `/auth`, `/users/me`, `/cars`, `/bookings` (user-facing), `/faqs` (public), `/help-topics` (public), `/feedback` (public POST), `/contact-us` (public POST) would remain as previously detailed, ensuring they return `ApiResponse<YourDTO>`)*
---

## Administrative Endpoints (`/admin`)

All endpoints under `/api/v1/admin` require appropriate ADMIN or SUPERADMIN authentication.
All successful data responses are wrapped in `ApiResponse<DataDTO>`. Error responses also use `ApiResponse` with an `errors` array.

### Admin - User Management (`/admin/users`)
*   **`GET /admin/users`**
    *   Description: Retrieves a list of all users (admin view).
    *   Response: `ApiResponse<List<UserResponseDTO>>`.
*   **`POST /admin/users`**
    *   Description: Creates a new user.
    *   Request Body: `UserCreateDTO`.
    *   Response: `201 Created` with `ApiResponse<UserResponseDTO>`.
*   **`GET /admin/users/{userUuid}`**
    *   Description: Retrieves a specific user by UUID.
    *   Response: `ApiResponse<UserResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/users/{userUuid}`**
    *   Description: Updates an existing user.
    *   Request Body: `UserUpdateDTO`.
    *   Response: `ApiResponse<UserResponseDTO>`. `404 Not Found`.
*   **`DELETE /admin/users/{userUuid}`**
    *   Description: Soft-deletes a user.
    *   Response: `204 No Content`. `404 Not Found`.
*   **`GET /admin/users/roles`**
    *   Description: Lists all available `Role` entities.
    *   Response: `ApiResponse<List<Role>>`. *(Consider RoleResponseDTO)*

### Admin - Car Management (`/admin/cars`)
*   **`GET /admin/cars`**
    *   Description: Retrieves a list of all cars (admin view).
    *   Response: `ApiResponse<List<CarResponseDTO>>`.
*   **`POST /admin/cars`**
    *   Description: Creates a new car.
    *   Request Body: `CarCreateDTO`.
    *   Response: `201 Created` with `ApiResponse<CarResponseDTO>`.
*   **`GET /admin/cars/{carUuid}`**
    *   Description: Retrieves a specific car by UUID.
    *   Response: `ApiResponse<CarResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/cars/{carUuid}`**
    *   Description: Updates an existing car.
    *   Request Body: `CarUpdateDTO`.
    *   Response: `ApiResponse<CarResponseDTO>`. `404 Not Found`.
*   **`DELETE /admin/cars/{carUuid}`**
    *   Description: Soft-deletes a car.
    *   Response: `204 No Content`. `404 Not Found`.

### Admin - Rental Management (`/admin/rentals`)
*   **`GET /admin/rentals`**
    *   Description: Retrieves a list of all rentals (admin view).
    *   Response: `ApiResponse<List<RentalResponseDTO>>`.
*   **`POST /admin/rentals`**
    *   Description: Creates a new rental (admin can specify user, car, etc.).
    *   Request Body: `BookingRequestDTO` (or `AdminRentalCreateDTO`).
    *   Response: `201 Created` with `ApiResponse<RentalResponseDTO>`.
*   **`GET /admin/rentals/{rentalUuid}`**
    *   Description: Retrieves a specific rental by UUID.
    *   Response: `ApiResponse<RentalResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/rentals/{rentalUuid}`**
    *   Description: Updates an existing rental.
    *   Request Body: `BookingUpdateDTO` (or `AdminRentalUpdateDTO`).
    *   Response: `ApiResponse<RentalResponseDTO>`. `404 Not Found`.
*   **`DELETE /admin/rentals/{rentalUuid}`**
    *   Description: Soft-deletes a rental.
    *   Response: `204 No Content`. `404 Not Found`.
*   **`POST /admin/rentals/{rentalUuid}/confirm`**
    *   Description: Admin confirms a rental.
    *   Response: `ApiResponse<RentalResponseDTO>`.
*   **`POST /admin/rentals/{rentalUuid}/cancel`**
    *   Description: Admin cancels a rental.
    *   Response: `ApiResponse<RentalResponseDTO>`.
*   **`POST /admin/rentals/{rentalUuid}/complete`**
    *   Description: Admin completes a rental.
    *   Query Parameter: `fineAmount` (double, optional).
    *   Response: `ApiResponse<RentalResponseDTO>`.

### Admin - FAQ Management (`/admin/faqs`)
*   **`GET /admin/faqs`** (If admin view is different from public `GET /faqs`)
    *   Description: Retrieves all FAQs for admin view.
    *   Response: `ApiResponse<List<FaqResponseDTO>>`.
*   **`POST /admin/faqs`**
    *   Description: Creates a new FAQ.
    *   Request Body: `FaqCreateDTO`.
    *   Response: `201 Created` with `ApiResponse<FaqResponseDTO>`.
*   **`GET /admin/faqs/{faqUuid}`**
    *   Description: Retrieves a specific FAQ by UUID.
    *   Response: `ApiResponse<FaqResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/faqs/{faqUuid}`**
    *   Description: Updates an existing FAQ.
    *   Request Body: `FaqUpdateDTO`.
    *   Response: `ApiResponse<FaqResponseDTO>`. `404 Not Found`.
*   **`DELETE /admin/faqs/{faqUuid}`**
    *   Description: Soft-deletes an FAQ.
    *   Response: `204 No Content`. `404 Not Found`.

### Admin - Help Topic Management (`/admin/help-topics`)
*   **`GET /admin/help-topics`** (If admin view is different from public `GET /help-topics`)
    *   Response: `ApiResponse<List<HelpCenterResponseDTO>>`.
*   **`POST /admin/help-topics`**
    *   Request Body: `HelpCenterCreateDTO`.
    *   Response: `201 Created` with `ApiResponse<HelpCenterResponseDTO>`.
*   **`GET /admin/help-topics/{topicUuid}`**
    *   Response: `ApiResponse<HelpCenterResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/help-topics/{topicUuid}`**
    *   Request Body: `HelpCenterUpdateDTO`.
    *   Response: `ApiResponse<HelpCenterResponseDTO>`. `404 Not Found`.
*   **`DELETE /admin/help-topics/{topicUuid}`**
    *   Response: `204 No Content`. `404 Not Found`.

### Admin - Feedback Management (`/admin/feedback`)
*   **`GET /admin/feedback`**
    *   Response: `ApiResponse<List<FeedbackResponseDTO>>`.
*   **`GET /admin/feedback/{feedbackUuid}`**
    *   Response: `ApiResponse<FeedbackResponseDTO>`. `404 Not Found`.
*   **`DELETE /admin/feedback/{feedbackUuid}`** (Soft delete)
    *   Response: `204 No Content`. `404 Not Found`.
    *   *(Note: Public POST for Feedback is at `/api/v1/feedback`)*

### Admin - Contact Us Submissions Management (`/admin/contact-us-submissions`)
*   **`GET /admin/contact-us-submissions`**
    *   Response: `ApiResponse<List<ContactUsResponseDTO>>`.
*   **`GET /admin/contact-us-submissions/{submissionUuid}`**
    *   Response: `ApiResponse<ContactUsResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/contact-us-submissions/{submissionUuid}`** (If admins can edit, e.g., add notes, change status)
    *   Request Body: `AdminContactUsUpdateDTO` (or a generic `ContactUsUpdateDTO`).
    *   Response: `ApiResponse<ContactUsResponseDTO>`.
*   **`DELETE /admin/contact-us-submissions/{submissionUuid}`** (Soft delete)
    *   Response: `204 No Content`. `404 Not Found`.
    *   *(Note: Public POST for Contact Us is at `/api/v1/contact-us`)*

### Admin - About Us Content Management (`/admin/about-us`)
*   **`GET /admin/about-us`** (If multiple entries possible, or to get the single one)
    *   Response: `ApiResponse<List<AboutUsResponseDTO>>` (or `ApiResponse<AboutUsResponseDTO>` if singleton).
*   **`POST /admin/about-us`** (To create an entry, if system allows more than one or none exists)
    *   Request Body: `AboutUsCreateDTO`.
    *   Response: `201 Created` with `ApiResponse<AboutUsResponseDTO>`.
*   **`GET /admin/about-us/{aboutUsUuid}`** (To get a specific entry by UUID if multiple allowed)
    *   Response: `ApiResponse<AboutUsResponseDTO>`. `404 Not Found`.
*   **`PUT /admin/about-us/{aboutUsUuid}`** (To update a specific entry by UUID)
    *   Request Body: `AboutUsUpdateDTO`.
    *   Response: `ApiResponse<AboutUsResponseDTO>`. `404 Not Found`.
    *   *(If "About Us" is a singleton, a common pattern is `PUT /api/v1/admin/about-us/content`)*
*   **`DELETE /admin/about-us/{aboutUsUuid}`** (If multiple entries allowed)
    *   Response: `204 No Content`. `404 Not Found`.

---

**3. Frontend Component Overview (Brief - for a general `DEVELOPMENT_GUIDE.md` or similar)**

```markdown
## Frontend Admin Components

The admin section of the frontend application provides interfaces for managing various entities. These components are typically found under `src/components/Admin/` or `src/views/Admin/`.

**General Pattern:**
*   **List Management Views (e.g., `AdminUserManagement.vue`, `AdminRentalManagement.vue`):**
    *   Fetch and display a list of entities from the corresponding `/api/v1/admin/{resource}` endpoint.
    *   Handle the `ApiResponse` wrapper from the backend.
    *   Provide functionality for searching, sorting, and pagination (if implemented).
    *   Allow navigation to create new entities (e.g., via a route like `/admin/{resource}/create`).
    *   Offer inline editing capabilities or links to a dedicated edit page.
    *   Implement soft deletion with confirmation modals.
    *   Use UUIDs for identifying specific resources when making API calls for update/delete/read-specific.
    *   Utilize shared modal components (`LoadingModal`, `SuccessModal`, `FailureModal`, `ConfirmationModal`) for user feedback.
    *   API calls are made using the central `api.js` Axios instance, which handles token attachment and refresh logic.
*   **Create/Edit Form Views (e.g., `AdminCreateUser.vue`, `AdminEditCar.vue`):**
    *   Provide forms for inputting data for new or existing entities.
    *   Use appropriate Request DTOs (`...CreateDTO`, `...UpdateDTO`) when submitting data to the backend.
    *   Handle `ApiResponse` for success or failure (validation errors from `GlobalExceptionHandler`).
    *   Client-side validation is often included for better UX before API submission.

**Key Vue Components for Admin:**
*   `AdminUserManagement.vue`: Manages users (list, create link, edit, delete).
*   `AdminCarManagement.vue`: Manages cars.
*   `AdminRentalManagement.vue`: Manages rentals (the post-booking stage).
*   `AdminBookingManagement.vue`: (If you have a separate one for pre-pickup bookings).
*   `AdminFaqManagement.vue`: Manages FAQs.
*   `AdminHelpCenterManagement.vue`: Manages Help Center topics.
*   `AdminFeedbackManagement.vue`: Manages user feedback.
*   `AdminContactUsManagement.vue`: Manages contact us submissions.
*   `AdminAboutUsManagement.vue`: Manages "About Us" content.

These components rely on `api.js` for backend communication and Vue Router for navigation. Data is passed between list views and edit/create views typically via route parameters (UUIDs).