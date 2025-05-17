# 🛠️ DEV_NOTES.md

This file contains tracked bugs, planned improvements, and technical notes for internal dev work.

---

## 🐛 Known Bugs

- [ ] Dashboard: formatting glitch on mobile view

# 🐛 Bugs to Fix
- [ ] Backend bookings sends whole user object including password in json response - switch to DTO
- [ ] Booking(admin create booking) form dates: date not getting to DB could be date picker not working 
- [ ] user profile update and read returning ful user object, switch to DTO


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
- [x] [Refactor] Centralize Axios auth headers using interceptor - Refactor all manual Axios Authorization headers to use global interceptor

---

## 📅 Next Sprint Focus

- Finalize booking CRUD cycle
- Start on chart stats integration
- UI polish for admin dashboard

---

