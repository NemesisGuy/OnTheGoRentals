package za.ac.cput.service.impl;

import jakarta.transaction.Transactional; // Use org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional; // Spring's Transactional
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException; // If needed for specific not found cases
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;
import za.ac.cput.factory.impl.RentalFactory; // Assuming this factory is still in use for initial creation parts
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService; // Import interface

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RentalServiceImpl.java
 * Implementation of the {@link IRentalService} interface.
 * Manages the lifecycle of rentals, including creation, retrieval, updates,
 * and status changes (confirm, cancel, complete). Interacts with User, Car,
 * and Booking services/repositories as needed.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service("rentalServiceImpl") // Explicit bean name
public class RentalServiceImpl implements IRentalService {

    private static final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final BookingRepository bookingRepository; // Assuming this is IBookingRepository if an interface exists
    private final RentalFactory rentalFactory; // If still used for initial complex creation beyond simple builder
    private final IUserService userService; // Use interface
    private final IBookingService bookingService; // Renamed from IBookingService to bookingService, using interface

    /**
     * Constructs the RentalServiceImpl with necessary repository and service dependencies.
     *
     * @param rentalRepository  The repository for rental persistence.
     * @param carRepository     The repository for car data access.
     * @param bookingRepository The repository for booking data access.
     * @param rentalFactory     The factory for creating initial rental instances (if complex).
     * @param userService       The service for user-related operations.
     * @param bookingService    The service for booking-related operations.
     */
    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository,
                             CarRepository carRepository,
                             BookingRepository bookingRepository, // Consider IBookingRepository
                             RentalFactory rentalFactory,
                             IUserService userService,         // Use interface
                             IBookingService bookingService) {  // Use interface
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.bookingRepository = bookingRepository;
        this.rentalFactory = rentalFactory;
        this.userService = userService;
        this.bookingService = bookingService; // Corrected assignment
        log.info("RentalServiceImpl initialized.");
    }

    // Commented out DTO conversion method as services should not deal with DTOs.
    /*
    public RentalDTO convertBookingToRental(Booking booking) {
        // ... logic ...
    }
    */

    /**
     * Checks if the car associated with the given rental is available.
     *
     * @param rental The {@link Rental} entity containing the car to check.
     * @return {@code true} if the car is available and not deleted, {@code false} otherwise.
     * @throws IllegalArgumentException if rental or its car is null.
     */
    public boolean isCarAvailable(Rental rental) {
        if (rental == null || rental.getCar() == null) {
            log.error("isCarAvailable check failed: Rental or its associated car is null.");
            throw new IllegalArgumentException("Rental and its car cannot be null for availability check.");
        }
        Car carToRent = rental.getCar();
        log.debug("Checking availability for car ID: {}", carToRent.getId());
        boolean available = carRepository.existsByIdAndAvailableTrueAndDeletedFalse(carToRent.getId());
        log.debug("Car ID: {} availability status: {}", carToRent.getId(), available);
        return available;
    }

    /**
     * Checks if a car, identified by its entity, is available.
     *
     * @param car The {@link Car} entity to check.
     * @return {@code true} if the car is available and not deleted, {@code false} otherwise.
     * @throws IllegalArgumentException if car is null.
     */
    public boolean isCarAvailableByCarId(Car car) {
        if (car == null) {
            log.error("isCarAvailableByCarId check failed: Car entity is null.");
            throw new IllegalArgumentException("Car entity cannot be null for availability check.");
        }
        log.debug("Checking availability for car (by entity) ID: {}", car.getId());
        boolean available = carRepository.existsByIdAndAvailableTrueAndDeletedFalse(car.getId());
        log.debug("Car ID: {} (by entity) availability status: {}", car.getId(), available);
        return available;
    }

    /**
     * {@inheritDoc}
     * Creates a new rental after checking car availability and if the user is currently renting.
     * Uses {@link RentalFactory} for initial creation if complex, then saves.
     * Assumes the input {@code rental} entity has necessary associations (User, Car) set.
     * The car associated with the rental will be marked as unavailable.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional // Spring's Transactional
    public Rental create(Rental rental) {
        User user = rental.getUser();
        Car car = rental.getCar();
        log.info("Attempting to create rental for User ID: {} and Car ID: {}", user != null ? user.getId() : "null", car != null ? car.getId() : "null");

        if (user == null || car == null) {
            log.error("Rental creation failed: User or Car is null in the provided rental entity.");
            throw new IllegalArgumentException("User and Car must be set in the Rental entity for creation.");
        }

        if (!isCarAvailableByCarId(car)) { // Check availability of the specific car instance
            log.warn("Rental creation failed: Car ID {} is not available.", car.getId());
            throw new CarNotAvailableException(generateCarNotAvailableErrorMessage(car));
        }
        if (isCurrentlyRenting(user)) {
            log.warn("Rental creation failed: User ID {} is already renting another car.", user.getId());
            throw new UserCantRentMoreThanOneCarException(generateUserRentingErrorMessage(user));
        }

        // Assuming rentalFactory.create() initializes the rental entity correctly
        // including setting a UUID, initial status, and default values if not already on 'rental'.
        // If 'rental' passed in is already fully formed by the controller (e.g. from DTO),
        // rentalFactory might just validate or ensure defaults.
        Rental newRental = rentalFactory.create(rental); // Or new Rental.Builder().copy(rental)... if factory is simple
        if (newRental.getUuid() == null) {
            newRental = new Rental.Builder().copy(newRental).setUuid(UUID.randomUUID()).build();
            log.debug("Generated UUID '{}' for new rental.", newRental.getUuid());
        }
        if (newRental.getStatus() == null) { // Set a default status if not provided
            newRental = new Rental.Builder().copy(newRental).setStatus(RentalStatus.PENDING_CONFIRMATION).build();
            log.debug("Defaulted rental status to PENDING_CONFIRMATION for rental UUID '{}'", newRental.getUuid());
        }
        newRental = new Rental.Builder().copy(newRental).setDeleted(false).build();


        // Mark car as unavailable
        Car updatedCar = Car.builder().copy(car).available(false).build();
        carRepository.save(updatedCar);
        log.info("Car ID {} marked as unavailable due to new rental.", car.getId());

        Rental savedRental = rentalRepository.save(newRental);
        log.info("Successfully created rental. ID: {}, UUID: '{}' for User ID: {}, Car ID: {}",
                savedRental.getId(), savedRental.getUuid(), user.getId(), car.getId());
        return savedRental;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental read(Integer id) {
        log.debug("Attempting to read rental by internal ID: {}", id);
        Rental rental = this.rentalRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (rental == null) log.warn("Rental not found or is deleted for ID: {}", id);
        else log.debug("Rental found for ID: {}. UUID: '{}'", id, rental.getUuid());
        return rental;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental read(UUID uuid) {
        log.debug("Attempting to read rental by UUID: '{}'", uuid);
        Rental rental = this.rentalRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (rental == null) log.warn("Rental not found or is deleted for UUID: '{}'", uuid);
        else log.debug("Rental found for UUID: '{}'. ID: {}", uuid, rental.getId());
        return rental;
    }

    /**
     * {@inheritDoc}
     * Updates an existing rental. If the rental's returnedDate is set (not null),
     * the associated car is marked as available. Otherwise, car availability is not changed by this update.
     * The input {@code rental} should be a fully formed entity with the desired new state,
     * typically constructed using the Builder pattern from the existing entity.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Rental update(Rental rentalWithUpdates) { // Parameter name implies it's the desired new state
        Integer rentalId = rentalWithUpdates.getId();
        log.info("Attempting to update rental with ID: {}", rentalId);
        if (rentalId == null || !rentalRepository.existsByIdAndDeletedFalse(rentalId)) {
            log.warn("Update failed: Rental not found or is deleted for ID: {}", rentalId);
            // Consider throwing ResourceNotFoundException if rentalId is not null but not found
            if (rentalId != null) throw new ResourceNotFoundException("Rental not found with ID: " + rentalId + " for update.");
            return null; // Or throw IllegalArgumentException if ID is null
        }

        // The 'rentalWithUpdates' IS the new state. The controller should have built it.
        // Example: existingRental = service.read(id);
        //          rentalWithUpdates = new Rental.Builder().copy(existingRental)...build();
        //          service.update(rentalWithUpdates);

        Car car = rentalWithUpdates.getCar();
        if (car == null) {
            log.error("Update failed for rental ID {}: Associated car is null.", rentalId);
            throw new IllegalStateException("Rental entity must have an associated car for update.");
        }

        // If rental is being marked as returned, make the car available
        if (rentalWithUpdates.getReturnedDate() != null && car.isAvailable() == false) {
            Car carToMakeAvailable = Car.builder().copy(car).available(true).build();
            carRepository.save(carToMakeAvailable);
            log.info("Car ID {} marked as available because rental ID {} is being updated with a return date.", car.getId(), rentalId);
        } else if (rentalWithUpdates.getReturnedDate() == null && car.isAvailable() == true && rentalWithUpdates.getStatus() != RentalStatus.CANCELLED) {
            // If rental is active/confirmed and return date is removed, ensure car is unavailable
            Car carToMakeUnavailable = Car.builder().copy(car).available(false).build();
            carRepository.save(carToMakeUnavailable);
            log.info("Car ID {} marked as unavailable because rental ID {} is being updated without a return date (and not cancelled).", car.getId(), rentalId);
        }


        Rental savedRental = rentalRepository.save(rentalWithUpdates);
        log.info("Successfully updated rental. ID: {}, UUID: '{}'", savedRental.getId(), savedRental.getUuid());
        return savedRental;
    }

    /**
     * Updates an existing rental identified by its integer ID, applying changes from the provided rental entity.
     * This is an overloaded method. It's generally recommended for the controller to fetch the existing entity,
     * build the updated state, and call {@link #update(Rental)}.
     *
     * @param id             The internal integer ID of the rental to update.
     * @param rentalWithData The {@link Rental} entity containing the new data. Its ID should match {@code id}.
     * @return The updated and persisted {@link Rental} entity, or {@code null} if not found.
     * @deprecated Prefer {@link #update(Rental)} where the controller manages fetching and building the updated entity.
     */
    @Deprecated
    @org.springframework.transaction.annotation.Transactional
    public Rental update(int id, Rental rentalWithData) {
        log.warn("Deprecated update(int, Rental) method called for rental ID: {}. Prefer update(Rental).", id);
        if (rentalWithData.getId() == 0 || rentalWithData.getId() != id) {
            log.error("Update failed: ID mismatch or rental data ID is null. Path ID: {}, Rental Data ID: {}", id, rentalWithData.getId());
            throw new IllegalArgumentException("Rental ID in path and data must match and not be null for update.");
        }
        return update(rentalWithData); // Delegate to the primary update method
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a rental by its internal integer ID.
     * If the rental was active, its associated car is marked as available.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete rental with ID: {}", id);
        Optional<Rental> rentalOpt = this.rentalRepository.findByIdAndDeletedFalse(id); // Fetch non-deleted first
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            Rental updatedRental = new Rental.Builder()
                    .copy(rental)
                    .setDeleted(true)
                    .setStatus(RentalStatus.CANCELLED) // Or a specific "DELETED" status if you have one
                    .build();
            rentalRepository.save(updatedRental);
            log.info("Rental ID: {} marked as deleted.", id);

            // If the rental was active or confirmed (car was unavailable), make car available
            if (rental.getCar() != null && !rental.getCar().isAvailable() &&
                    (rental.getStatus() == RentalStatus.ACTIVE || rental.getStatus() == RentalStatus.CONFIRMED || rental.getStatus() == RentalStatus.IN_PROGRESS)) {
                Car carToMakeAvailable = Car.builder().copy(rental.getCar()).available(true).build();
                carRepository.save(carToMakeAvailable);
                log.info("Car ID {} associated with deleted rental ID {} marked as available.", rental.getCar().getId(), id);
            }
            return true;
        }
        log.warn("Soft-delete failed: Rental not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Rental> getAll() {
        log.debug("Fetching all non-deleted rentals.");
        return this.rentalRepository.findAllByDeletedFalse();
    }

    /**
     * Retrieves a list of rentals associated with cars that are currently available.
     * Note: This method's name "getAllAvailableCars" is misleading as it returns Rentals, not Cars.
     * It filters existing rentals based on the current availability of their associated cars.
     *
     * @return A list of {@link Rental} entities whose associated cars are available.
     * @deprecated Consider renaming or refactoring for clarity. Perhaps "getRentalsWithAvailableCars".
     */
    @Deprecated
    public List<Rental> getAllAvailableCars() {
        log.warn("getAllAvailableCars() called; this method returns Rentals, not Cars, and filters by car availability. Consider refactoring.");
        List<Rental> allRentals = rentalRepository.findAllByDeletedFalse();
        List<Rental> rentalsWithAvailableCars = filterAvailableCars(allRentals);
        log.debug("Found {} rentals associated with available cars.", rentalsWithAvailableCars.size());
        return rentalsWithAvailableCars;
    }

    private List<Rental> filterAvailableCars(List<Rental> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return Collections.emptyList();
        }
        return rentals.stream()
                .filter(this::isCarAvailable) // Uses the instance method
                .collect(Collectors.toList());
    }

    private String generateCarNotAvailableErrorMessage(Car car) {
        if (car == null) return "The specified car is not available.";
        return String.format("%s %s (License: %s) is not available for rental at this time.",
                car.getMake(), car.getModel(), car.getLicensePlate());
    }

    private String generateUserRentingErrorMessage(User user) {
        if (user == null) return "The user is already renting a car.";
        Rental currentRental = getCurrentRental(user); // Assumes this fetches the active rental
        if (currentRental != null && currentRental.getCar() != null) {
            Car rentedCar = currentRental.getCar();
            return String.format("%s %s is currently renting %s %s (License: %s).",
                    user.getFirstName(), user.getLastName(),
                    rentedCar.getMake(), rentedCar.getModel(), rentedCar.getLicensePlate());
        }
        return String.format("%s %s is currently renting another car.", user.getFirstName(), user.getLastName());
    }

    /**
     * Checks if a user is currently renting any car (i.e., has an active rental without a return date).
     *
     * @param user The {@link User} to check.
     * @return {@code true} if the user has an active rental, {@code false} otherwise.
     * @throws IllegalArgumentException if user is null.
     */
    public boolean isCurrentlyRenting(User user) {
        if (user == null || user.getId() == null) {
            log.error("isCurrentlyRenting check failed: User or User ID is null.");
            throw new IllegalArgumentException("User and User ID cannot be null for checking current rentals.");
        }
        log.debug("Checking if User ID: {} is currently renting.", user.getId());
        List<Rental> activeRentals = rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(user.getId());
        boolean isRenting = !activeRentals.isEmpty();
        log.debug("User ID: {} is currently renting: {}", user.getId(), isRenting);
        return isRenting;
    }

    /**
     * Retrieves the current active rental for a given user, if any.
     * An active rental is one that is not deleted and does not have a return date.
     *
     * @param user The {@link User} whose active rental is to be found.
     * @return The active {@link Rental} if one exists, otherwise {@code null}.
     * @throws IllegalArgumentException if user is null.
     */
    public Rental getCurrentRental(User user) {
        if (user == null || user.getId() == null) {
            log.error("getCurrentRental failed: User or User ID is null.");
            throw new IllegalArgumentException("User and User ID cannot be null for fetching current rental.");
        }
        log.debug("Fetching current rental for User ID: {}", user.getId());
        List<Rental> activeRentals = rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(user.getId());
        if (!activeRentals.isEmpty()) {
            Rental currentRental = activeRentals.get(0); // Assuming a user can only have one active rental
            log.debug("User ID: {} has an active rental. ID: {}, UUID: '{}'", user.getId(), currentRental.getId(), currentRental.getUuid());
            if (activeRentals.size() > 1) {
                log.warn("User ID: {} has multiple active rentals (returnedDate is null). Data integrity issue suspected. Returning the first one found.", user.getId());
            }
            return currentRental;
        }
        log.debug("User ID: {} has no active rentals.", user.getId());
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Integer id) {
        log.debug("Checking if rental exists by ID: {}", id);
        boolean exists = rentalRepository.existsByIdAndDeletedFalse(id);
        log.debug("Rental with ID: {} exists (and not deleted): {}", id, exists);
        return exists;
    }

    /**
     * Checks if a given car is booked (has a confirmed booking) within a specified date range.
     *
     * @param car       The {@link Car} to check.
     * @param startDate The start of the date range.
     * @param endDate   The end of the date range.
     * @return {@code true} if the car has a confirmed booking overlapping the range, {@code false} otherwise.
     */
    public boolean isCarBooked(Car car, LocalDateTime startDate, LocalDateTime endDate) {
        if (car == null) throw new IllegalArgumentException("Car cannot be null for booking check.");
        log.debug("Checking if Car ID: {} is booked between {} and {}", car.getId(), startDate, endDate);
        List<Booking> activeBookings = bookingRepository.findByCarAndStatusAndEndDateAfterAndStartDateBeforeAndDeletedFalse(
                car, RentalStatus.CONFIRMED.name(), startDate, endDate // Assuming status is String
        );
        boolean booked = !activeBookings.isEmpty();
        log.debug("Car ID: {} is booked in the range: {}", car.getId(), booked);
        return booked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Rental> getRentalHistoryByUser(User user) {
        if (user == null || user.getId() == null) {
            log.error("getRentalHistoryByUser failed: User or User ID is null.");
            throw new IllegalArgumentException("User and User ID cannot be null for fetching rental history.");
        }
        log.debug("Fetching rental history for User ID: {}", user.getId());
        List<Rental> rentals = rentalRepository.findByUserIdAndDeletedFalse(user.getId());
        log.debug("Found {} rental entries for User ID: {}", rentals.size(), user.getId());
        return rentals;
    }

    /**
     * Checks if a car has any active, confirmed bookings.
     *
     * @param car The {@link Car} to check.
     * @return {@code true} if the car has any confirmed bookings, {@code false} otherwise.
     */
    public boolean isCarBooked(Car car) {
        if (car == null) throw new IllegalArgumentException("Car cannot be null for booking check.");
        log.debug("Checking if Car ID: {} has any confirmed bookings.", car.getId());
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatusAndDeletedFalse(car.getId(), RentalStatus.CONFIRMED.name());
        boolean booked = !activeBookings.isEmpty();
        log.debug("Car ID: {} has active confirmed bookings: {}", car.getId(), booked);
        return booked;
    }

    /**
     * Finds a rental by its UUID, ensuring it's not soft-deleted.
     *
     * @param rentalUuid The UUID of the rental. (Parameter name changed for clarity)
     * @return The {@link Rental} entity if found and not deleted, otherwise {@code null}.
     */
    public Rental findByUuid(UUID rentalUuid) { // Renamed parameter
        log.debug("Attempting to find rental by UUID: '{}' (custom findByUuid method)", rentalUuid);
        return read(rentalUuid); // Delegate to the interface's read(UUID) method for consistency
    }

    /**
     * {@inheritDoc}
     * Confirms a rental, setting its status to CONFIRMED and marking the associated car as unavailable.
     * The rental's issuedDate is set to the current time.
     * @throws IllegalStateException if the rental is not in PENDING_CONFIRMATION status.
     * @throws ResourceNotFoundException if the rental or its car is not found.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Rental confirmRentalByUuid(UUID rentalUuid) {
        log.info("Attempting to confirm rental with UUID: '{}'", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) {
            log.warn("Confirmation failed: Rental not found with UUID: '{}'", rentalUuid);
            throw new ResourceNotFoundException("Rental not found with UUID: " + rentalUuid + " for confirmation.");
        }
        if (rental.getStatus() != RentalStatus.PENDING_CONFIRMATION) {
            log.warn("Confirmation failed for rental UUID '{}': Rental is not PENDING_CONFIRMATION. Current status: {}", rentalUuid, rental.getStatus());
            throw new IllegalStateException("Only PENDING_CONFIRMATION rentals can be confirmed. Current status: " + rental.getStatus());
        }
        Car car = rental.getCar();
        if (car == null) {
            log.error("Confirmation failed for rental UUID '{}': Associated car not found.", rentalUuid);
            throw new ResourceNotFoundException("Associated car not found for rental UUID: " + rentalUuid + " during confirmation.");
        }
        // Additional check: is car still available? (Though create should have handled it)
        if (!car.isAvailable()) {
            log.warn("Confirmation warning for rental UUID '{}': Car ID {} is already marked as unavailable. Proceeding with confirmation.", rentalUuid, car.getId());
            // This might be acceptable if another process just marked it. If it's a strict rule, throw exception.
            // throw new CarNotAvailableException("Car ID " + car.getId() + " is no longer available for rental confirmation.");
        }

        Rental updatedRental = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.CONFIRMED)
                .setIssuedDate(LocalDateTime.now()) // Set issue date upon confirmation
                .build();
        Car updatedCar = Car.builder().copy(car).available(false).build();

        carRepository.save(updatedCar);
        log.info("Car ID {} marked as unavailable upon rental UUID '{}' confirmation.", car.getId(), rentalUuid);
        Rental savedRental = rentalRepository.save(updatedRental);
        log.info("Successfully confirmed rental. ID: {}, UUID: '{}'", savedRental.getId(), savedRental.getUuid());
        return savedRental;
    }

    /**
     * {@inheritDoc}
     * Cancels a rental, setting its status to CANCELLED and marking the associated car as available.
     * @throws IllegalStateException if the rental is IN_PROGRESS or COMPLETED.
     * @throws ResourceNotFoundException if the rental or its car is not found.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Rental cancelRentalByUuid(UUID rentalUuid) {
        log.info("Attempting to cancel rental with UUID: '{}'", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) {
            log.warn("Cancellation failed: Rental not found with UUID: '{}'", rentalUuid);
            throw new ResourceNotFoundException("Rental not found with UUID: " + rentalUuid + " for cancellation.");
        }

        if (rental.getStatus() == RentalStatus.IN_PROGRESS || rental.getStatus() == RentalStatus.COMPLETED) {
            log.warn("Cancellation failed for rental UUID '{}': Cannot cancel a rental that is {} or {}.",
                            rentalUuid, RentalStatus.IN_PROGRESS, RentalStatus.COMPLETED);
            throw new IllegalStateException("Cannot cancel a rental that is " + rental.getStatus() + ".");
        }

        Rental updatedRental = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.CANCELLED)
                // Optionally clear dates or set a cancellation date if your model supports it
                // .setReturnedDate(LocalDateTime.now()) // Or a specific cancellation timestamp field
                .build();

        Car car = rental.getCar();
        if (car == null) {
            log.error("Critical error during cancellation of rental UUID '{}': Associated car not found. Data inconsistency suspected.", rentalUuid);
            // Even if car is null, proceed to cancel rental status, but log error.
            // throw new ResourceNotFoundException("Associated car not found for rental UUID: " + rentalUuid + " during cancellation.");
        } else {
            // Only make car available if it was associated with this rental and potentially made unavailable by it.
            // If car was already available (e.g. PENDING_CONFIRMATION was cancelled), this is fine.
            if (!car.isAvailable()) {
                Car updatedCar = Car.builder().copy(car).available(true).build();
                carRepository.save(updatedCar);
                log.info("Car ID {} marked as available upon rental UUID '{}' cancellation.", car.getId(), rentalUuid);
            } else {
                log.debug("Car ID {} associated with rental UUID '{}' was already available. No change to car availability on cancellation.", car.getId(), rentalUuid);
            }
        }

        Rental savedRental = rentalRepository.save(updatedRental);
        log.info("Successfully cancelled rental. ID: {}, UUID: '{}'", savedRental.getId(), savedRental.getUuid());
        return savedRental;
    }

    /**
     * {@inheritDoc}
     * Completes a rental, setting its status to COMPLETED, recording the actual return date,
     * applying any fines, and marking the associated car as available.
     * @throws IllegalStateException if the rental is not in IN_PROGRESS or CONFIRMED status.
     * @throws ResourceNotFoundException if the rental or its car is not found.
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public Rental completeRentalByUuid(UUID rentalUuid, double fineAmount) {
        log.info("Attempting to complete rental with UUID: '{}', Fine amount: {}", rentalUuid, fineAmount);
        Rental rental = read(rentalUuid);
        if (rental == null) {
            log.warn("Completion failed: Rental not found with UUID: '{}'", rentalUuid);
            throw new ResourceNotFoundException("Rental not found with UUID: " + rentalUuid + " for completion.");
        }

        // Typically, a rental can only be completed if it's IN_PROGRESS or sometimes directly from CONFIRMED
        // if it was never picked up but administrative completion is needed.
        if (rental.getStatus() != RentalStatus.IN_PROGRESS && rental.getStatus() != RentalStatus.CONFIRMED && rental.getStatus() != RentalStatus.ACTIVE) {
            log.warn("Completion failed for rental UUID '{}': Rental cannot be completed from its current state: {}", rentalUuid, rental.getStatus());
            throw new IllegalStateException("Rental cannot be completed from its current state: " + rental.getStatus());
        }
        if (fineAmount < 0) {
            log.warn("Completion attempt for rental UUID '{}' with negative fine amount: {}. Using 0.0.", rentalUuid, fineAmount);
            fineAmount = 0.0; // Or throw IllegalArgumentException
        }

        Rental updatedRental = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.COMPLETED)
                .setReturnedDate(LocalDateTime.now()) // Actual return time
                .setFine((int) fineAmount) // Assuming fine is stored as int in your entity
                .build();

        Car car = rental.getCar();
        if (car == null) {
            log.error("Critical error during completion of rental UUID '{}': Associated car not found. Data inconsistency suspected.", rentalUuid);
            // Proceed to complete rental status, but log error.
            // throw new ResourceNotFoundException("Associated car not found for rental UUID: " + rentalUuid + " during completion.");
        } else {
            Car updatedCar = Car.builder().copy(car).available(true).build();
            carRepository.save(updatedCar);
            log.info("Car ID {} marked as available upon rental UUID '{}' completion.", car.getId(), rentalUuid);
        }

        Rental savedRental = rentalRepository.save(updatedRental);
        log.info("Successfully completed rental. ID: {}, UUID: '{}'. Fine: {}", savedRental.getId(), savedRental.getUuid(), fineAmount);
        return savedRental;
    }

    @Override
    public List<Rental> findByUserIdAndReturnedDateIsNullAndDeletedFalse(Integer userId) {
        return rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(userId);
    }
}