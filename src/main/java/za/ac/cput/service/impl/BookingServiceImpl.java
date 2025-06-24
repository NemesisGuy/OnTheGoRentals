package za.ac.cput.service.impl;

// Use org.springframework.transaction.annotation.Transactional

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.InvalidDateRangeException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IUserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
// import java.util.stream.Collectors; // Not used in current methods

/**
 * BookingServiceImpl.java
 * Implementation of the {@link IBookingService} interface.
 * Manages the lifecycle of bookings, including creation with validation,
 * status changes (confirm, cancel), and retrieval.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 * <p>
 * Author: [Original Author - Cwenga Dlova or Peter Buckingham]
 * Date: [Original Date]
 * Updated by: Peter Buckingham
 * Updated: 2025-06-01 // Date updated to reflect current modification
 */
@Service("bookingServiceImpl") // Explicit bean name, class name conventional
public class BookingServiceImpl implements IBookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class); // Logger for this class

    private final BookingRepository bookingRepository;
    private final ICarService carService; // Use ICarService for car operations
    private final IUserService userService; // If needed for user validation/fetching

    /**
     * Constructs the BookingServiceImpl with necessary repository and service dependencies.
     *
     * @param bookingRepository The repository for booking persistence.
     * @param carService        The service for car-related operations (e.g., reading car details, checking availability).
     * @param userService       The service for user-related operations.
     */
    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ICarService carService, IUserService userService) {
        this.bookingRepository = bookingRepository;
        this.carService = carService;
        this.userService = userService;
        log.info("BookingServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Booking create(Booking bookingDetails) {
        log.info("Attempting to create booking for User UUID: {} and Car UUID: {}", // Changed log to use UUIDs
                bookingDetails.getUser() != null ? bookingDetails.getUser().getUuid() : "N/A",
                bookingDetails.getCar() != null ? bookingDetails.getCar().getUuid() : "N/A");

        if (bookingDetails.getUser() == null || bookingDetails.getCar() == null ||
                bookingDetails.getStartDate() == null || bookingDetails.getEndDate() == null) {
            log.error("Booking creation failed: User, Car, StartDate, or EndDate is null in bookingDetails.");
            throw new IllegalArgumentException("User, Car, Start Date, and End Date must be provided for booking.");
        }
        if (bookingDetails.getUser().getUuid() == null || bookingDetails.getCar().getUuid() == null) {
            log.error("Booking creation failed: User UUID or Car UUID is null.");
            throw new IllegalArgumentException("User UUID and Car UUID must be valid for booking creation.");
        }


        LocalDateTime startDate = bookingDetails.getStartDate();
        LocalDateTime endDate = bookingDetails.getEndDate();

        if (!endDate.isAfter(startDate)) {
            log.warn("Booking creation failed: End date ({}) must be after start date ({}).", endDate, startDate);
            throw new InvalidDateRangeException("Booking end date must be after the booking start date.");
        }

        long minimumDurationHours = 1;
        if (ChronoUnit.HOURS.between(startDate, endDate) < minimumDurationHours) {
            log.warn("Booking creation failed: Booking duration ({}) is less than the minimum {} hours.",
                    ChronoUnit.HOURS.between(startDate, endDate), minimumDurationHours);
            throw new InvalidDateRangeException("Booking duration must be at least " + minimumDurationHours + " hour(s).");
        }

        Car car = carService.read(bookingDetails.getCar().getUuid());
        if (car == null) {
            log.warn("Booking creation failed: Car with UUID {} not found.", bookingDetails.getCar().getUuid());
            throw new ResourceNotFoundException("Car with UUID " + bookingDetails.getCar().getUuid() + " not found.");
        }

        User user = userService.read(bookingDetails.getUser().getUuid());
        if (user == null) {
            log.warn("Booking creation failed: User with UUID {} not found.", bookingDetails.getUser().getUuid());
            throw new ResourceNotFoundException("User with UUID " + bookingDetails.getUser().getUuid() + " not found.");
        }


        if (isCarDoubleBooked(car, startDate, endDate, null)) {
            log.warn("Booking creation failed: Car UUID {} is already booked for the selected period ({} to {}).",
                    car.getUuid(), startDate, endDate);
            throw new CarNotAvailableException("Car " + car.getMake() + " " + car.getModel() + " is not available for the selected dates.");
        }
        log.debug("Car UUID {} availability confirmed for the period.", car.getUuid());

        Booking bookingToSave = new Booking.Builder()
                .copy(bookingDetails)
                .setUser(user)
                .setCar(car)
                .setStatus(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(bookingToSave);
        log.info("Successfully created booking. ID: {}, UUID: '{}', Status: {}",
                savedBooking.getId(), savedBooking.getUuid(), savedBooking.getStatus());
        return savedBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Booking createBooking(Booking booking) {
        // This method is a simplified version. We assume necessary fields are present.
        // For robust creation, the primary `create(Booking bookingDetails)` method is preferred.
        log.warn("Calling simplified createBooking(Booking) method. Consider using the more detailed create(Booking) method with full validation. Booking for Car UUID: {}",
                booking.getCar() != null ? booking.getCar().getUuid() : "N/A");

        if (booking.getCar() == null || booking.getCar().getUuid() == null ||
                booking.getUser() == null || booking.getUser().getUuid() == null ||
                booking.getStartDate() == null || booking.getEndDate() == null) { // Added date checks
            log.error("Simplified createBooking failed: Car UUID, User UUID, StartDate or EndDate is missing.");
            throw new IllegalArgumentException("Car UUID, User UUID, StartDate and EndDate must be provided.");
        }

        Car car = carService.read(booking.getCar().getUuid());
        if (car == null) {
            throw new ResourceNotFoundException("Car not found with UUID: " + booking.getCar().getUuid());
        }
        User user = userService.read(booking.getUser().getUuid());
        if (user == null) {
            throw new ResourceNotFoundException("User not found with UUID: " + booking.getUser().getUuid());
        }

        // Simplified availability check - more robust check is in isCarDoubleBooked
        // Note: This simplified check doesn't exclude a current booking ID if this method were used for updates.
        List<Booking> activeBookings = bookingRepository.findOverlappingBookings(
                car.getId(), BookingStatus.CONFIRMED, booking.getStartDate(), booking.getEndDate()
        );
        if (!activeBookings.isEmpty()) {
            log.warn("Simplified createBooking: Car UUID {} is already booked for the selected period.", car.getUuid());
            throw new CarNotAvailableException("Car is already booked for the selected period.");
        }

        Booking bookingToSave = new Booking.Builder()
                .copy(booking)
                .setUser(user)
                .setCar(car)
                .setStatus(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(bookingToSave);
        log.info("Booking created via simplified method. ID: {}, UUID: '{}'", savedBooking.getId(), savedBooking.getUuid());
        return savedBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Booking confirmBooking(int bookingId) {
        log.info("Attempting to confirm booking with ID: {}", bookingId);
        Booking booking = read(bookingId);
        if (booking == null) {
            log.warn("Confirmation failed: Booking not found with ID: {}", bookingId);
            throw new ResourceNotFoundException("Booking not found with ID: " + bookingId + " for confirmation.");
        }

        // A booking can be "confirmed" if it's already CONFIRMED (idempotent)
        // or if it's in a state that allows confirmation (e.g., PENDING_CONFIRMATION, though not used in current flow)
        if (booking.getStatus() == BookingStatus.USER_CANCELLED ||
                booking.getStatus() == BookingStatus.ADMIN_CANCELLED ||
                booking.getStatus() == BookingStatus.RENTAL_INITIATED ||
                booking.getStatus() == BookingStatus.NO_SHOW) {
            log.warn("Booking ID {} cannot be confirmed. Current status: {}", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking cannot be confirmed from status: " + booking.getStatus());
        }

        Booking updatedBooking = new Booking.Builder().copy(booking).setStatus(BookingStatus.CONFIRMED).build();
        Booking savedBooking = bookingRepository.save(updatedBooking); // save returns the merged/updated entity
        log.info("Booking ID: {} confirmed successfully. Status: {}", savedBooking.getId(), savedBooking.getStatus()); // Log status from saved entity
        return savedBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Booking cancelBooking(int bookingId) {
        log.info("Attempting to cancel booking with ID: {}", bookingId);
        Booking booking = read(bookingId);
        if (booking == null) {
            log.warn("Cancellation failed: Booking not found with ID: {}", bookingId);
            throw new ResourceNotFoundException("Booking not found with ID: " + bookingId + " for cancellation.");
        }

        // Business rule: Can only cancel if not yet RENTAL_INITIATED.
        // Other statuses like ADMIN_CANCELLED, USER_CANCELLED, NO_SHOW are also terminal for cancellation.
        if (booking.getStatus() == BookingStatus.RENTAL_INITIATED ||
                booking.getStatus() == BookingStatus.USER_CANCELLED || // Already cancelled
                booking.getStatus() == BookingStatus.ADMIN_CANCELLED || // Already cancelled
                booking.getStatus() == BookingStatus.NO_SHOW) {
            log.warn("Booking ID {} cannot be cancelled. Current status: {}", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking cannot be cancelled from status: " + booking.getStatus());
        }
        // If it's CONFIRMED, a user can cancel it.
        // If it's PENDING (if that status existed), it could also be cancelled.

        Booking updatedBooking = new Booking.Builder().copy(booking).setStatus(BookingStatus.USER_CANCELLED).build();
        Booking savedBooking = bookingRepository.save(updatedBooking); // save returns the merged/updated entity
        log.info("Booking ID: {} cancelled successfully. Status: {}", savedBooking.getId(), savedBooking.getStatus()); // Log status from saved
        return savedBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Booking> getUserBookings(int userId) {
        log.debug("Fetching all non-deleted bookings for User ID: {}", userId);
        return bookingRepository.findByUserIdAndDeletedFalse(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Booking read(int id) {
        log.debug("Reading booking by internal ID: {}", id);
        return bookingRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Booking read(UUID uuid) {
        log.debug("Reading booking by UUID: '{}'", uuid);
        return bookingRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Booking update(Booking bookingWithUpdates) {
        Integer bookingId = bookingWithUpdates.getId();
        log.info("Attempting to update booking with ID: {}", bookingId);

        if (bookingId == null || bookingId == 0) {
            log.error("Update failed: Booking ID is missing or invalid (0).");
            throw new IllegalArgumentException("A valid Booking ID must be provided for update.");
        }

        Booking existingBooking = bookingRepository.findByIdAndDeletedFalse(bookingId)
                .orElseThrow(() -> {
                    log.warn("Update failed: Booking not found or is deleted for ID: {}", bookingId);
                    return new ResourceNotFoundException("Booking not found with ID: " + bookingId + " for update.");
                });

        // Prevent status changes that should go through dedicated methods (e.g., cancel, confirm, initiate rental)
        // Admins might be allowed to change some fields, but status lifecycle should be respected.
        if (bookingWithUpdates.getStatus() != existingBooking.getStatus()) {
            if (existingBooking.getStatus() == BookingStatus.RENTAL_INITIATED ||
                    existingBooking.getStatus() == BookingStatus.USER_CANCELLED ||
                    existingBooking.getStatus() == BookingStatus.ADMIN_CANCELLED ||
                    existingBooking.getStatus() == BookingStatus.NO_SHOW) {
                log.warn("Update failed for Booking ID {}: Status cannot be changed from a terminal state like {}", bookingId, existingBooking.getStatus());
                throw new IllegalStateException("Cannot update status from " + existingBooking.getStatus() + " via general update.");
            }
            // Add more specific status transition checks if needed for admin updates
        }


        Car carForUpdate = existingBooking.getCar(); // Default to existing car
        boolean datesOrCarChanged = false;

        if (bookingWithUpdates.getCar() != null && !bookingWithUpdates.getCar().getUuid().equals(existingBooking.getCar().getUuid())) {
            carForUpdate = carService.read(bookingWithUpdates.getCar().getUuid());
            if (carForUpdate == null) {
                throw new ResourceNotFoundException("Car for update not found with UUID: " + bookingWithUpdates.getCar().getUuid());
            }
            datesOrCarChanged = true;
        }

        if (bookingWithUpdates.getStartDate() != null && !bookingWithUpdates.getStartDate().equals(existingBooking.getStartDate())) {
            datesOrCarChanged = true;
        }
        if (bookingWithUpdates.getEndDate() != null && !bookingWithUpdates.getEndDate().equals(existingBooking.getEndDate())) {
            datesOrCarChanged = true;
        }

        if (datesOrCarChanged) {
            LocalDateTime checkStartDate = bookingWithUpdates.getStartDate() != null ? bookingWithUpdates.getStartDate() : existingBooking.getStartDate();
            LocalDateTime checkEndDate = bookingWithUpdates.getEndDate() != null ? bookingWithUpdates.getEndDate() : existingBooking.getEndDate();

            if (!checkEndDate.isAfter(checkStartDate)) {
                log.warn("Booking update failed for ID {}: End date must be after start date.", bookingId);
                throw new InvalidDateRangeException("Booking end date must be after the booking start date for update.");
            }
            if (isCarDoubleBooked(carForUpdate, checkStartDate, checkEndDate, bookingId)) {
                log.warn("Booking update failed for ID {}: Car UUID {} is not available for the new period.", bookingId, carForUpdate.getUuid());
                throw new CarNotAvailableException("Car " + carForUpdate.getMake() + " " + carForUpdate.getModel() + " is not available for the new selected dates.");
            }
        }

        Booking entityToSave = new Booking.Builder()
                .copy(existingBooking) // Start with existing to preserve immutable fields
                // Apply changes from bookingWithUpdates carefully
                .setStartDate(bookingWithUpdates.getStartDate() != null ? bookingWithUpdates.getStartDate() : existingBooking.getStartDate())
                .setEndDate(bookingWithUpdates.getEndDate() != null ? bookingWithUpdates.getEndDate() : existingBooking.getEndDate())
                .setCar(carForUpdate) // Use the potentially re-fetched carForUpdate
                .setStatus(bookingWithUpdates.getStatus() != null ? bookingWithUpdates.getStatus() : existingBooking.getStatus())
                .setDriver(bookingWithUpdates.getDriver() != null ? bookingWithUpdates.getDriver() : existingBooking.getDriver()) // Allow driver to be updated/set/cleared
                // ID, UUID, User, CreatedAt, Deleted are preserved from existingBooking by starting with copy()
                .build();

        Booking savedBooking = bookingRepository.save(entityToSave);
        log.info("Successfully updated booking. ID: {}, UUID: '{}'", savedBooking.getId(), savedBooking.getUuid());
        return savedBooking;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean delete(int id) {
        log.info("Attempting to soft-delete booking with ID: {}", id);
        Optional<Booking> bookingOpt = bookingRepository.findByIdAndDeletedFalse(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            // Business rule: For admin delete, might be more lenient or use ADMIN_CANCELLED
            // If this delete is user-initiated, it should perhaps go through cancelBooking.
            // For a generic admin delete, ADMIN_CANCELLED makes sense.
            if (booking.getStatus() == BookingStatus.RENTAL_INITIATED) {
                log.warn("Soft-delete failed for Booking ID {}: Cannot delete booking with status {}", id, booking.getStatus());
                throw new IllegalStateException("Cannot delete a booking that has already been processed into a rental.");
            }

            Booking deletedBooking = new Booking.Builder().copy(booking)
                    .setDeleted(true)
                    // If not already cancelled, set to ADMIN_CANCELLED to reflect it was an admin action
                    .setStatus(booking.getStatus() == BookingStatus.USER_CANCELLED || booking.getStatus() == BookingStatus.ADMIN_CANCELLED ?
                            booking.getStatus() : BookingStatus.ADMIN_CANCELLED)
                    .build();
            bookingRepository.save(deletedBooking);
            log.info("Successfully soft-deleted booking ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: Booking not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Booking> getAll() {
        log.debug("Fetching all non-deleted bookings.");
        return bookingRepository.findByDeletedFalse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Booking> findBookingsForCollectionToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);
        log.debug("Finding confirmed bookings for collection between {} and {}", startOfDay, endOfDay);
        return bookingRepository.findByStatusAndStartDateBetweenAndDeletedFalse(
                BookingStatus.CONFIRMED, startOfDay, endOfDay
        );
    }

    /**
     * Private helper to check for double bookings for a given car and period,
     * excluding a specific booking by its ID (useful for updates).
     */
    private boolean isCarDoubleBooked(Car car, LocalDateTime proposedStartDate, LocalDateTime proposedEndDate, Integer excludeBookingId) {
        log.debug("Checking for double booking. Car ID: {}, Period: {} to {}. Excluding Booking ID (if any): {}",
                car.getId(), proposedStartDate, proposedEndDate, excludeBookingId);

        List<Booking> overlappingBookings = bookingRepository
                .findOverlappingBookings(car.getId(), BookingStatus.CONFIRMED, proposedStartDate, proposedEndDate);

        if (overlappingBookings.isEmpty()) {
            log.debug("No overlapping CONFIRMED bookings found for Car ID: {}.", car.getId());
            return false;
        }

        if (excludeBookingId != null) {
            boolean otherBookingOverlaps = overlappingBookings.stream()
                    .anyMatch(b -> b.getId() != excludeBookingId.intValue());
            if (otherBookingOverlaps) {
                log.warn("Car ID: {} is double-booked for the period. Another CONFIRMED booking overlaps (excluding Booking ID {}).", car.getId(), excludeBookingId);
            } else {
                log.debug("No *other* CONFIRMED bookings overlap for Car ID: {} (excluding Booking ID {}).", car.getId(), excludeBookingId);
            }
            return otherBookingOverlaps;
        }
        log.warn("Car ID: {} is double-booked for the period. Found {} overlapping CONFIRMED booking(s).", car.getId(), overlappingBookings.size());
        return true;
    }
}