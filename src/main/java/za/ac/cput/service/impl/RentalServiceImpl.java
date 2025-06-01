package za.ac.cput.service.impl;

// Using org.springframework.transaction.annotation.Transactional for Spring's AOP-based transactions
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;
import za.ac.cput.factory.impl.RentalFactory; // Assuming still used for some default/complex initializations
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.*; // Import all service interfaces from the package

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RentalServiceImpl.java
 * Implementation of the {@link IRentalService} interface.
 * Manages the lifecycle of rentals, including creation, retrieval, updates,
 * and status changes (confirm, cancel, complete). Interacts with User, Car,
 * and Booking services/repositories as needed.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - e.g. 10 April 2023]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Service("rentalServiceImpl")
public class RentalServiceImpl implements IRentalService {

    private static final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository; // Direct repository for car state updates
    private final BookingRepository bookingRepository; // For checking booking overlaps
    private final RentalFactory rentalFactory; // If it provides more than simple builder instantiation
    private final IUserService userService;
    private final IBookingService bookingService;
    private final ICarService carService; // For high-level car operations like read by UUID
    private final IDriverService driverService;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository,
                             CarRepository carRepository,
                             BookingRepository bookingRepository,
                             RentalFactory rentalFactory,
                             IUserService userService,
                             IBookingService bookingService,
                             ICarService carService,
                             IDriverService driverService) {
        log.info("Initializing RentalServiceImpl with dependencies.");
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.bookingRepository = bookingRepository;
        this.rentalFactory = rentalFactory;
        this.userService = userService;
        this.bookingService = bookingService;
        this.carService = carService;
        this.driverService = driverService;
        log.info("RentalServiceImpl initialized.");
    }

    // --- Private Helper Methods ---
    private boolean isCarAvailableByCarId(Car car) {
        if (car == null || car.getId() == 0) { // Check for null or unpersisted car ID
            log.error("isCarAvailableByCarId check failed: Car entity or its ID is null/invalid.");
            throw new IllegalArgumentException("Car entity and its ID must be valid for availability check.");
        }
        log.debug("Checking availability for car (by entity) ID: {}", car.getId());
        // CarRepository should have a method that checks the 'available' flag and 'deleted' flag
        boolean available = carRepository.existsByIdAndAvailableTrueAndDeletedFalse(car.getId());
        log.debug("Car ID: {} (by entity) availability status: {}", car.getId(), available);
        return available;
    }

    private String generateCarNotAvailableErrorMessage(Car car) {
        if (car == null) return "The specified car is not available.";
        return String.format("%s %s (License: %s) is not available for rental at this time.",
                car.getMake(), car.getModel(), car.getLicensePlate());
    }

    private String generateUserRentingErrorMessage(User user) {
        if (user == null) return "The user is already renting a car.";
        Rental currentRental = getCurrentRental(user);
        if (currentRental != null && currentRental.getCar() != null) {
            Car rentedCar = currentRental.getCar();
            return String.format("%s %s is currently renting %s %s (License: %s).",
                    user.getFirstName(), user.getLastName(),
                    rentedCar.getMake(), rentedCar.getModel(), rentedCar.getLicensePlate());
        }
        return String.format("%s %s is currently renting another car.", user.getFirstName(), user.getLastName());
    }

    // --- Public Service Methods from IRentalService ---

    @Override
    @Transactional
    public Rental create(Rental rentalDataFromController) {
        User user = rentalDataFromController.getUser();
        Car carFromInput = rentalDataFromController.getCar(); // Car from input DTO/initial build

        log.info("Attempting to create rental for User ID: {} and Car ID: {}",
                user != null ? user.getId() : "null", carFromInput != null ? carFromInput.getId() : "null");

        if (user == null || carFromInput == null) {
            log.error("Rental creation failed: User or Car is null in the provided rental entity.");
            throw new IllegalArgumentException("User and Car must be set in the Rental entity for creation.");
        }
        if (user.getId() == null || carFromInput.getId() == 0) { // Assuming Car ID is int and 0 means unpersisted
            log.error("Rental creation failed: User ID or Car ID is null/invalid. Ensure entities are persisted or IDs are set.");
            throw new IllegalArgumentException("User and Car must have valid IDs for rental creation.");
        }

        if (!isCarAvailableByCarId(carFromInput)) {
            log.warn("Rental creation failed: Car ID {} is not available.", carFromInput.getId());
            throw new CarNotAvailableException(generateCarNotAvailableErrorMessage(carFromInput));
        }
        if (isCurrentlyRenting(user)) {
            log.warn("Rental creation failed: User ID {} is already renting another car.", user.getId());
            throw new UserCantRentMoreThanOneCarException(generateUserRentingErrorMessage(user));
        }

        // Mark car as unavailable *before* creating the final rental entity state
        Car carToUpdate = carRepository.findById(carFromInput.getId()).orElseThrow(() -> new ResourceNotFoundException("Car not found during rental creation"));
        Car carMadeUnavailable = Car.builder().copy(carToUpdate).setAvailable(false).build();
        Car persistedUnavailableCar = carRepository.save(carMadeUnavailable);
        log.info("Car ID {} marked as unavailable due to new rental.", persistedUnavailableCar.getId());

        // Now, use the 'persistedUnavailableCar' when building the rentalToCreate
        // The rentalFactory.create might apply defaults or use builder logic.
        // Assuming rentalDataFromController might be partial, factory completes it.
        Rental tempRentalForFactory = new Rental.Builder()
                .copy(rentalDataFromController)
                .setCar(persistedUnavailableCar) // Use the car instance that's now unavailable
                .build();
        Rental rentalToCreate = rentalFactory.create(tempRentalForFactory); // Factory applies further logic/defaults

        // Ensure essential fields are set by factory or @PrePersist, or set explicitly here if needed
        if (rentalToCreate.getStatus() == null) {
            rentalToCreate = new Rental.Builder().copy(rentalToCreate).setStatus(RentalStatus.ACTIVE).build();
        }
        if (rentalToCreate.getUuid() == null) {
            rentalToCreate = new Rental.Builder().copy(rentalToCreate).setUuid(UUID.randomUUID()).build();
        }
        rentalToCreate = new Rental.Builder().copy(rentalToCreate).setDeleted(false).build();


        Rental savedRental = rentalRepository.save(rentalToCreate);
        log.info("Successfully created rental. ID: {}, UUID: '{}' for User ID: {}, Car ID: {}",
                savedRental.getId(), savedRental.getUuid(), savedRental.getUser().getId(), savedRental.getCar().getId());
        return savedRental;
    }

    @Override
    @Transactional
    public Rental createRentalFromBooking(UUID bookingUuid, UUID issuerId, UUID driverUuid, LocalDateTime actualIssuedDate) {
        log.info("Attempting to create rental from Booking UUID: {}", bookingUuid);

        Booking booking = bookingService.read(bookingUuid);
        if (booking == null) {
            log.warn("Rental creation from booking failed: Booking UUID '{}' not found.", bookingUuid);
            throw new ResourceNotFoundException("Booking not found with UUID: " + bookingUuid);
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            log.warn("Rental creation from booking failed: Booking UUID '{}' is not in CONFIRMED status. Current status: {}", bookingUuid, booking.getStatus());
            throw new IllegalStateException("Booking must be in CONFIRMED status to create a rental. Current status: " + booking.getStatus());
        }

        Car carFromBooking = booking.getCar();
        if (carFromBooking == null) {
            log.error("Rental creation from booking failed: Booking UUID '{}' has no associated car.", bookingUuid);
            throw new ResourceNotFoundException("Car not found for booking UUID: " + bookingUuid);
        }
        // Fetch the definitive current state of the car
        Car carToRent = carService.read(carFromBooking.getUuid());
        if (carToRent == null || !carToRent.isAvailable()) {
            log.warn("Rental creation from booking failed: Car UUID '{}' for booking '{}' is no longer available or not found.",
                    carFromBooking.getUuid(), bookingUuid);
            throw new CarNotAvailableException("The car (UUID: " + carFromBooking.getUuid() + ") for this booking is no longer available.");
        }

        User user = booking.getUser();
        Driver driver = null;
        if (driverUuid != null) {
            driver = driverService.read(driverUuid);
            if (driver == null) {
                log.warn("Driver with UUID {} not found during rental creation from booking. Proceeding without driver.", driverUuid);
            }
        } else if (booking.getDriver() != null) {
            driver = booking.getDriver();
        }

        // Mark car as unavailable
        Car carMadeUnavailable = Car.builder().copy(carToRent).setAvailable(false).build();
        carService.update(carMadeUnavailable); // Use ICarService to update
        log.info("Car ID: {}, UUID: {} marked as unavailable via carService.", carMadeUnavailable.getId(), carMadeUnavailable.getUuid());

        Rental newRental = new Rental.Builder()
                .setUser(user)
                .setCar(carMadeUnavailable) // Use the car instance that is now marked unavailable
                .setDriver(driver)
                .setIssuedDate(actualIssuedDate != null ? actualIssuedDate : LocalDateTime.now())
                .setExpectedReturnDate(booking.getEndDate())
                .setStatus(RentalStatus.ACTIVE)
                .setIssuer(issuerId != null ? issuerId : UUID.randomUUID()) // Assuming 0 or another value means system/unassigned
                .setDeleted(false)
                // Rental's @PrePersist or Builder.build() will handle UUID, createdAt, updatedAt
                .build();

        Rental createdRental = rentalRepository.save(newRental);
        log.info("Successfully created Rental ID: {}, UUID: {} from Booking UUID: {}",
                createdRental.getId(), createdRental.getUuid(), bookingUuid);

        Booking bookingToUpdate = new Booking.Builder().copy(booking)
                .setStatus(BookingStatus.RENTAL_INITIATED)
                .build();
        bookingService.update(bookingToUpdate);
        log.info("Booking UUID: {} status updated to RENTAL_INITIATED.", booking.getUuid());

        return createdRental;
    }


    @Override
    public Rental read(Integer id) {
        log.debug("Attempting to read rental by internal ID: {}", id);
        return rentalRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public Rental read(UUID uuid) {
        log.debug("Attempting to read rental by UUID: '{}'", uuid);
        return rentalRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    @Override
    @Transactional
    public Rental update(Rental rentalWithUpdates) {
        Integer rentalId = rentalWithUpdates.getId();
        log.info("Attempting to update rental. ID: {}", rentalId);

        if (rentalId == null) {
            log.error("Update failed: Rental ID is null.");
            throw new IllegalArgumentException("Rental ID cannot be null for an update operation.");
        }
        // Fetch existing to ensure it's a valid, non-deleted rental before proceeding
        Rental existingRental = rentalRepository.findByIdAndDeletedFalse(rentalId)
                .orElseThrow(() -> {
                    log.warn("Update failed: Rental not found or is deleted for ID: {}", rentalId);
                    return new ResourceNotFoundException("Rental not found with ID: " + rentalId + " for update.");
                });

        Car carFromUpdate = rentalWithUpdates.getCar();
        if (carFromUpdate == null || carFromUpdate.getId() == 0) { // Or carFromUpdate.getId() == null if Integer
            log.error("Update failed for rental ID {}: Associated car or car ID is null/invalid in the update data.", rentalId);
            throw new IllegalStateException("Rental entity must have a valid associated car for update.");
        }

        // Fetch the definitive current state of the car to be associated, especially if carUuid might have changed
        Car carToAssociateWithRental = carService.read(carFromUpdate.getUuid());
        if (carToAssociateWithRental == null) {
            log.error("Update failed for rental ID {}: Car UUID {} provided in update data not found.", rentalId, carFromUpdate.getUuid());
            throw new ResourceNotFoundException("Car with UUID " + carFromUpdate.getUuid() + " not found for rental update.");
        }


        boolean carAvailabilityNeedsUpdate = false;
        Car carStateToSave = carToAssociateWithRental;

        // Logic for car availability:
        // 1. If rental is being COMPLETED or CANCELLED, car becomes available.
        // 2. If rental is becoming ACTIVE (and wasn't returned), car becomes unavailable.
        if ((rentalWithUpdates.getStatus() == RentalStatus.COMPLETED || rentalWithUpdates.getStatus() == RentalStatus.CANCELLED) &&
                !carToAssociateWithRental.isAvailable()) {
            carStateToSave = Car.builder().copy(carToAssociateWithRental).setAvailable(true).build();
            carAvailabilityNeedsUpdate = true;
            log.info("Car ID {} will be marked as available as rental ID {} status is {} or {}.",
                    carToAssociateWithRental.getId(), rentalId, RentalStatus.COMPLETED, RentalStatus.CANCELLED);
        } else if (rentalWithUpdates.getStatus() == RentalStatus.ACTIVE &&
                rentalWithUpdates.getReturnedDate() == null && // Ensure it's not an update that also sets return date
                carToAssociateWithRental.isAvailable()) {
            carStateToSave = Car.builder().copy(carToAssociateWithRental).setAvailable(false).build();
            carAvailabilityNeedsUpdate = true;
            log.info("Car ID {} will be marked as unavailable as rental ID {} status is ACTIVE and not returned.",
                    carToAssociateWithRental.getId(), rentalId);
        }

        if (carAvailabilityNeedsUpdate) {
            carRepository.save(carStateToSave); // Persist car state change
        }

        // Ensure the rentalWithUpdates object uses the potentially updated carStateToSave
        Rental finalRentalStateToSave = new Rental.Builder()
                .copy(rentalWithUpdates) // This has the ID, UUID, and all desired fields from controller
                .setCar(carStateToSave)   // Ensure it references the car instance whose availability we might have changed
                .build(); // Builder sets updatedAt

        Rental savedRental = rentalRepository.save(finalRentalStateToSave);
        log.info("Successfully updated rental. ID: {}, UUID: '{}'", savedRental.getId(), savedRental.getUuid());
        return savedRental;
    }

    @Override
    @Deprecated
    @Transactional
    public Rental update(int id, Rental rentalWithData) {
        // ... (implementation as before, delegating to update(Rental)) ...
        log.warn("Deprecated update(int, Rental) method called for rental ID: {}. Prefer update(Rental).", id);
        if (rentalWithData.getId() == 0 || rentalWithData.getId() != id) { // ID is primitive int, check against 0
            log.error("Update failed: ID mismatch or rental data ID is 0. Path ID: {}, Rental Data ID: {}", id, rentalWithData.getId());
            throw new IllegalArgumentException("Rental ID in path and data must match and not be 0 for update via deprecated method.");
        }
        Rental rentalToUpdate = new Rental.Builder().copy(rentalWithData).setId(id).build();
        return update(rentalToUpdate); // This will re-fetch using existsByIdAndDeletedFalse
    }


    // In RentalServiceImpl.java
    @Override
    @Transactional
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete rental with ID: {}", id);
        Optional<Rental> rentalOpt = rentalRepository.findByIdAndDeletedFalse(id);
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            Car originalCar = rental.getCar();
            Car carToPersistInRental = originalCar; // Assume original car initially

            // If the rental was active or confirmed and car was unavailable, make car available
            if (originalCar != null && !originalCar.isAvailable() &&
                    (rental.getStatus() == RentalStatus.ACTIVE /* || rental.getStatus() == RentalStatus.CONFIRMED */)) {
                Car carMadeAvailable = Car.builder().copy(originalCar).setAvailable(true).build();
                carRepository.save(carMadeAvailable); // Save the car with updated availability
                carToPersistInRental = carMadeAvailable; // This is the car instance that should be in the saved Rental
                log.info("Car ID {} associated with deleted (was ACTIVE) rental ID {} marked as available.", originalCar.getId(), id);
            }

            Rental deletedRental = new Rental.Builder()
                    .copy(rental)
                    .setDeleted(true)
                    .setStatus(RentalStatus.CANCELLED)
                    .setCar(carToPersistInRental) // <<< IMPORTANT: Set the car that reflects correct availability
                    .build();
            rentalRepository.save(deletedRental);
            log.info("Rental ID: {} marked as deleted and status set to CANCELLED.", id);
            return true;
        }
        log.warn("Soft-delete failed: Rental not found or already deleted for ID: {}", id);
        return false;
    }
    @Override
    public List<Rental> getAll() {
        log.debug("Fetching all non-deleted rentals.");
        return rentalRepository.findAllByDeletedFalse();
    }

    @Override
    public boolean isCurrentlyRenting(User user) {
        if (user == null || user.getId() == null) {
            log.error("isCurrentlyRenting check failed: User or User ID is null.");
            throw new IllegalArgumentException("User and User ID cannot be null for checking current rentals.");
        }
        log.debug("Checking if User ID: {} is currently renting (status ACTIVE, not returned, not deleted).", user.getId());
        List<Rental> activeRentals = rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(user.getId(), RentalStatus.ACTIVE);
        boolean isRenting = !activeRentals.isEmpty();
        log.debug("User ID: {} is currently renting: {}", user.getId(), isRenting);
        return isRenting;
    }

    @Override
    public Rental getCurrentRental(User user) {
        // ... (implementation as before) ...
        if (user == null || user.getId() == null) {
            log.error("getCurrentRental failed: User or User ID is null.");
            throw new IllegalArgumentException("User and User ID cannot be null for fetching current rental.");
        }
        log.debug("Fetching current rental for User ID: {} (status ACTIVE, not returned, not deleted).", user.getId());
        List<Rental> activeRentals = rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(user.getId(), RentalStatus.ACTIVE);
        if (!activeRentals.isEmpty()) {
            Rental currentRental = activeRentals.get(0);
            log.debug("User ID: {} has an active rental. ID: {}, UUID: '{}'", user.getId(), currentRental.getId(), currentRental.getUuid());
            if (activeRentals.size() > 1) {
                log.warn("User ID: {} has multiple rentals with status ACTIVE and no return date. Data integrity issue suspected. Returning the first one found.", user.getId());
            }
            return currentRental;
        }
        log.debug("User ID: {} has no active rentals.", user.getId());
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        log.debug("Checking if rental exists by ID: {}", id);
        return rentalRepository.existsByIdAndDeletedFalse(id);
    }

    @Override
    public List<Rental> getRentalHistoryByUser(User user) {
        // ... (implementation as before) ...
        if (user == null || user.getId() == null) {
            log.error("getRentalHistoryByUser failed: User or User ID is null.");
            throw new IllegalArgumentException("User and User ID cannot be null for fetching rental history.");
        }
        log.debug("Fetching all non-deleted rental history for User ID: {}", user.getId());
        List<Rental> rentals = rentalRepository.findByUserIdAndDeletedFalse(user.getId());
        log.debug("Found {} rental entries for User ID: {}", rentals.size(), user.getId());
        return rentals;
    }

    @Override
    @Transactional
    public Rental confirmRentalByUuid(UUID rentalUuid) {
        log.info("Attempting to confirm rental with UUID: '{}'", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) {
            log.warn("Confirmation failed: Rental not found with UUID: '{}'", rentalUuid);
            throw new ResourceNotFoundException("Rental not found with UUID: " + rentalUuid + " for confirmation.");
        }
       // If the rental is ALREADY ACTIVE (default on creation from booking), this might just be a verification step.
        if (rental.getStatus() != RentalStatus.ACTIVE  /* if status was from booking initially */) {
            // This logic depends on what "confirm" means in your workflow for a Rental.
            // If a Rental is created directly in ACTIVE state from a Booking, this check might change.
            // Let's assume for now it should be coming from a state that *can* be confirmed into ACTIVE
            // or it's re-affirming an ACTIVE state.
            log.warn("Rental UUID '{}' current status is {}. Business logic for 'confirm' might need review if it's not already active or ready to be active.", rentalUuid, rental.getStatus());
            // For now, we will ensure it becomes/stays ACTIVE
        }

        Car car = rental.getCar();
        if (car == null) {
            log.error("Confirmation failed for rental UUID '{}': Associated car not found.", rentalUuid);
            throw new ResourceNotFoundException("Associated car not found for rental UUID: " + rentalUuid);
        }

        Car carToPersist = car;
        if (car.isAvailable()) {
            carToPersist = Car.builder().copy(car).setAvailable(false).build();
            carRepository.save(carToPersist);
            log.info("Car ID {} marked as unavailable upon rental UUID '{}' confirmation.", car.getId(), rentalUuid);
        }

        // Build the new state for the rental
        Rental rentalToSave = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.ACTIVE) // Ensure status is ACTIVE
                .setCar(carToPersist) // Ensure it has the car (possibly updated availability)
                // If confirmation also implies setting issuedDate to now:
                // .setIssuedDate(LocalDateTime.now())
                .build();

        Rental savedRental = rentalRepository.save(rentalToSave);
        log.info("Successfully confirmed rental (or ensured ACTIVE state). ID: {}, UUID: '{}'", savedRental.getId(), savedRental.getUuid());
        return savedRental;
    }


    @Override
    @Transactional
    public Rental cancelRentalByUuid(UUID rentalUuid) {
        // ... (implementation as before, ensure car is updated with builder) ...
        log.info("Attempting to cancel rental with UUID: '{}'", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) {
            log.warn("Cancellation failed: Rental not found with UUID: '{}'", rentalUuid);
            throw new ResourceNotFoundException("Rental not found with UUID: " + rentalUuid);
        }
        if (rental.getStatus() == RentalStatus.COMPLETED) {
            log.warn("Cancellation failed for rental UUID '{}': Cannot cancel a COMPLETED rental.", rentalUuid);
            throw new IllegalStateException("Cannot cancel a rental that is already COMPLETED.");
        }
        if (rental.getStatus() == RentalStatus.CANCELLED) {
            log.info("Rental UUID '{}' is already CANCELLED.", rentalUuid);
            return rental;
        }

        Car car = rental.getCar();
        Car carToPersistInRental = car;
        if (car != null && !car.isAvailable()) {
            Car carMadeAvailable = Car.builder().copy(car).setAvailable(true).build();
            carRepository.save(carMadeAvailable);
            carToPersistInRental = carMadeAvailable;
            log.info("Car ID {} marked as available upon rental UUID '{}' cancellation.", car.getId(), rentalUuid);
        }

        Rental cancelledRental = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.CANCELLED)
                .setCar(carToPersistInRental) // Set the car with updated availability
                .build();
        Rental savedRental = rentalRepository.save(cancelledRental);
        log.info("Successfully cancelled rental. ID: {}, UUID: '{}'", savedRental.getId(), savedRental.getUuid());
        return savedRental;
    }

    @Override
    @Transactional
    public Rental completeRentalByUuid(UUID rentalUuid, double fineAmount) {
        // ... (implementation as before, ensure car is updated with builder) ...
        log.info("Attempting to complete rental with UUID: '{}', Fine amount: {}", rentalUuid, fineAmount);
        Rental rental = read(rentalUuid);
        if (rental == null) {
            log.warn("Completion failed: Rental not found with UUID: '{}'", rentalUuid);
            throw new ResourceNotFoundException("Rental not found with UUID: " + rentalUuid);
        }
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            log.warn("Completion failed for rental UUID '{}': Rental is not ACTIVE. Current status: {}", rentalUuid, rental.getStatus());
            throw new IllegalStateException("Only ACTIVE rentals can be completed. Current status: " + rental.getStatus());
        }
        // ... (fine amount check) ...

        Car car = rental.getCar();
        Car carToPersistInRental = car;
        if (car != null) {
            Car carMadeAvailable = Car.builder().copy(car).setAvailable(true).build();
            carRepository.save(carMadeAvailable);
            carToPersistInRental = carMadeAvailable;
            log.info("Car ID {} marked as available upon rental UUID '{}' completion.", car.getId(), rentalUuid);
        }

        Rental completedRentalUpdate = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.COMPLETED)
                .setReturnedDate(LocalDateTime.now())
                .setFine((int) fineAmount)
                .setCar(carToPersistInRental)
                .build();
        Rental savedRental = rentalRepository.save(completedRentalUpdate);
        log.info("Successfully completed rental. ID: {}, UUID: '{}'. Fine: {}", savedRental.getId(), savedRental.getUuid(), fineAmount);
        return savedRental;
    }

    // --- Methods for Due/Overdue Rentals (implementation as before) ---
    @Override
    public List<Rental> findRentalsDueToday() { /* ... as before ... */
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);
        log.debug("Finding rentals due today (between {} and {}) with status ACTIVE and no actual return date.", startOfDay, endOfDay);
        return rentalRepository.findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(
                startOfDay, endOfDay, RentalStatus.ACTIVE);
    }
    @Override
    public List<Rental> findOverdueRentals() { /* ... as before ... */
        LocalDateTime now = LocalDateTime.now();
        log.debug("Finding overdue rentals (expected return date before {} AND status ACTIVE AND no actual return date).", now);
        return rentalRepository.findByExpectedReturnDateBeforeAndStatusAndReturnedDateIsNullAndDeletedFalse(
                now, RentalStatus.ACTIVE);
    }
    @Override
    public List<Rental> findRentalsDueOnDate(LocalDate specificDate) { /* ... as before ... */
        if (specificDate == null) {
            log.warn("Specific date provided for findRentalsDueOnDate is null. Returning empty list.");
            return Collections.emptyList();
        }
        LocalDateTime startOfDay = specificDate.atStartOfDay();
        LocalDateTime endOfDay = specificDate.atTime(23, 59, 59, 999999999);
        log.debug("Finding rentals due on date {} (between {} and {}) with status ACTIVE and no actual return date.", specificDate, startOfDay, endOfDay);
        return rentalRepository.findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(
                startOfDay, endOfDay, RentalStatus.ACTIVE);
    }

    @Override
    public List<Rental> findByUserIdAndReturnedDateIsNullAndDeletedFalse(Integer userId) {
        log.debug("Fetching active (not returned, not deleted) rentals for User ID: {}", userId);
        return rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(userId);
    }
}