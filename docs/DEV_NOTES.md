# 🛠️ DEV_NOTES.md

This file contains tracked bugs, planned improvements, and technical notes for internal dev work.

---

## 🐛 Known Bugs

- [ ] Dashboard: formatting glitch on mobile view

# 🐛 Bugs to Fix

- [ ] Backend bookings sends whole user object including password in json response - switch to DTO
- [ ] Booking(admin create booking) form dates: date not getting to DB could be date picker not working
- [x] user profile update and read returning ful user object, switch to DTO (UserResponseDTO is used in UserController
  and AdminUserController for profile read/update responses)

# ✅ Done

- [x] Fixed loading modal timing in booking form
- [x] Booking modal not hiding loader on cancel
- [x] Dashboard chart icon missing
- [x] Role null pointer error when fetching users

---

## ✨ Planned Improvements

- [ ] Add form validation to booking form (required fields)
- [ ] Disable submit button during async request
- [ ] Add toast/snackbar alerts for quick feedback
- [x] Add chart analytics (e.g., bar chart for bookings per week)

---

## 📌 Notes & Tech Debt

- Spring Boot controller should null-check `roles` or use `Optional.ofNullable()`
- Add global Axios interceptor for token expiry
- Extract modals to reusable logic-based components
- Use enums for booking status (Pending, Confirmed, Returned)

---

## ✅ Recently Done

- [x] Booking creation API integrated and tested
- [x] Loading modal shows correctly on API call start
- [x] Added confirmation modal before creating booking
- [x] [Refactor] `/api/admin/users/list/all` fetch into separate method
- [x] [Refactor] Centralize Axios auth headers using interceptor - Refactor all manual Axios Authorization headers to
  use global interceptor

---

## 📅 Next Sprint Focus

- Finalize booking CRUD cycle
- Start on chart stats integration
- UI polish for admin dashboard

---

## 🛠️ Refactoring Summary

### Admin Controllers and Vue.js Components Refactor

(2025-05-27)

Completed a comprehensive refactoring of all administrative controllers
(AboutUs, Car, ContactUs, Driver, Faq, HelpCenter, Rental, User)
and their corresponding Vue.js management components.

This effort establishes consistent patterns across the admin interface:

Backend (Admin Controllers):

- Standardized base API paths to `/api/v1/admin/{resource_plural}`.
- Implemented RESTful CRUD operations using standard HTTP methods.
- External resource identification now consistently uses UUIDs in path
  variables. Integer IDs are for internal database relations.
- Request DTOs (`...CreateDTO`, `...UpdateDTO`) are used for request
  bodies, incorporating Jakarta Bean Validation.
- Response DTOs (`...ResponseDTO`) are used for all API responses,
  ensuring a controlled data contract, wrapped in a standard
  `ApiResponse` object.
- Controllers handle mapping between DTOs and Entities using static
  Mapper classes.
- Service layers maintain focus on working with Entities and business logic.
- Entities now consistently use an immutable builder pattern, and
  mappers leverage these builders.
- Removed direct factory usage from controllers.

Frontend (Admin Vue Components - e.g., RentalManagement, UsersManagement):

- Updated to consume the `ApiResponse` wrapper for all API calls.
- Adapted to use UUIDs when interacting with specific resources via API.
- Data fetching, inline editing, creation, and deletion logic now use
  the appropriate Request/Response DTOs.
- Implemented robust loading states (including shimmer effects) and
  error handling using modals.
- Standardized on Promise-based .then().catch().finally() for async
  operations, removing direct token management (delegated to api.js).
- Corrected template bindings and data flow issues for display and editing.

This refactoring significantly improves API clarity, consistency,
maintainability, and the separation of concerns for all administrative
functions of the application.