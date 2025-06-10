package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;
import za.ac.cput.factory.impl.RentalFactory;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * RentalServiceImpl.java
 * Implementation of the {@link IRentalService} interface.
 * Manages the lifecycle of rentals, including creation, retrieval, updates,
 * and status changes. This version uses a builder with an 'applyTo'
 * method to update managed JPA entities, preserving the entity's immutable public API.
 *
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Service("rentalServiceImpl")
@Transactional
public class RentalServiceImpl implements IRentalService {

    private static final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final ICarService carService;
    private final IBookingService bookingService;
    private final IUserService userService;
    private final IDriverService driverService;
    private final RentalFactory rentalFactory;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, CarRepository carRepository, ICarService carService, IBookingService bookingService, IUserService userService, IDriverService driverService, RentalFactory rentalFactory) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.carService = carService;
        this.bookingService = bookingService;
        this.userService = userService;
        this.driverService = driverService;
        this.rentalFactory = rentalFactory;
        log.info("RentalServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * This method is for creating a new Rental from raw data, strictly using the builder pattern.
     */
    @Override
    public Rental create(Rental rentalData) {
        log.info("Attempting to create a new rental for user UUID {}", rentalData.getUser().getUuid());

        User user = userService.read(rentalData.getUser().getUuid());
        Car car = carService.read(rentalData.getCar().getUuid());

        if (car == null || !car.isAvailable()) {
            throw new CarNotAvailableException("Car is not available for rental.");
        }
        if (isCurrentlyRenting(user)) {
            throw new UserCantRentMoreThanOneCarException("User is already renting a car.");
        }

        // Use the builder to modify the managed car entity
        new Car.Builder().copy(car).setAvailable(false).applyTo(car);
        log.info("Car UUID {} marked as unavailable for new rental.", car.getUuid());

        // Construct the final rental object using the builder, NOT setters
        Rental rentalToCreate = new Rental.Builder()
                .copy(rentalData) // Copy initial data (dates, etc.) from the controller
                .setUser(user)   // Set the managed User entity
                .setCar(car)     // Set the managed Car entity (which is now unavailable)
                .setStatus(RentalStatus.ACTIVE) // Ensure status is set correctly
                .build();        // Build the final object

        Rental savedRental = rentalRepository.save(rentalToCreate);
        log.info("Successfully created new rental with UUID: {}", savedRental.getUuid());
        return savedRental;
    }

    /**
     * {@inheritDoc}
     * This method orchestrates the entire business process of converting a confirmed booking
     * into an active rental. It operates within a single database transaction, ensuring that
     * all steps either succeed together or fail together, leaving the database in a
     * consistent state.
     */
    @Override
    @Transactional
    public Rental createRentalFromBooking(UUID bookingUuid, UUID issuerId, UUID driverUuid, LocalDateTime actualIssuedDate) {
        log.info("Service: Starting transaction to create rental from Booking UUID: {}", bookingUuid);

        // 1. Fetch the source Booking
        Booking booking = bookingService.read(bookingUuid);

        // 2. Validate the state of the booking
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking must be in CONFIRMED status to create a rental. Current status: " + booking.getStatus());
        }

        // 3. Validate the state of the Car
        Car carToRent = carService.read(booking.getCar().getUuid());
        if (!carToRent.isAvailable()) {
            throw new CarNotAvailableException("Car '" + carToRent.getMake() + " " + carToRent.getModel() + "' is no longer available.");
        }

        // 4. Gather all required entities
        User user = booking.getUser();
        // The driver can be newly assigned at pickup or taken from the original booking
        Driver driver = (driverUuid != null) ? driverService.read(driverUuid) : booking.getDriver();
        User issuer = userService.read(issuerId); // Fetch the staff member performing the action

        // 5. Create the new Rental entity using the factory
        // The factory should correctly set the `returnedDate` to null by default.
        Rental newRental = rentalFactory.create(
                user,
                carToRent,
                driver,
                issuer.getUuid(), // Pass the full User entity
                actualIssuedDate != null ? actualIssuedDate : LocalDateTime.now(), // The RENTAL START DATE
                booking.getEndDate(), // The EXPECTED RETURN DATE
                null // The EXPECTED RETURN DATE
        );

        // The actual return date is intentionally null at creation.
        log.debug("New rental object created. Actual return date is null as expected.");

        Rental createdRental = rentalRepository.save(newRental);
        log.info("Successfully created and saved Rental UUID {}", createdRental.getUuid());

        // 6. Update the Car's status and persist the change
        Car.Builder carBuilder = new Car.Builder();
        carBuilder.copy(carToRent)
                .setAvailable(false) // Mark the car as unavailable
                .applyTo(carToRent); // Apply changes to the managed entity
        carRepository.save(carToRent);
        log.info("Car UUID {} marked as unavailable.", carToRent.getUuid());

        // 7. Update the Booking's status and persist the change
        Booking.Builder bookingBuilder = new Booking.Builder();
        bookingBuilder.copy(booking)
                .setStatus(BookingStatus.RENTAL_INITIATED) // Update the booking status
                .applyTo(booking); // Apply changes to the managed entity

        bookingService.update(booking);
        log.info("Booking UUID {} status updated to RENTAL_INITIATED.", booking.getUuid());

        // The transaction commits here, making all changes permanent.
        return createdRental;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental read(Integer id) {
        log.debug("Reading rental by ID: {}", id);
        return rentalRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental read(UUID uuid) {
        log.debug("Reading rental by UUID: {}", uuid);
        return rentalRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

    /**
     * {@inheritDoc}
     * This method uses the Builder's `applyTo` method to update the managed entity,
     * preserving the immutable public API of the Rental class.
     */
    @Override
    public Rental update(Rental rentalWithUpdates) {
        Integer rentalId = rentalWithUpdates.getId();
        log.info("Attempting to update rental ID: {}", rentalId);

        if (rentalId == null) throw new IllegalArgumentException("Rental ID is required for an update.");

        Rental existingRental = rentalRepository.findByIdAndDeletedFalse(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with ID: " + rentalId));

        updateCarAvailabilityOnStatusChange(existingRental.getCar(), existingRental.getStatus(), rentalWithUpdates.getStatus());

        new Rental.Builder()
                .copy(rentalWithUpdates)
                .setCreatedAt(existingRental.getCreatedAt())
                .setUuid(existingRental.getUuid())
                .applyTo(existingRental);

        log.info("Rental ID {} successfully updated. New status: {}", rentalId, existingRental.getStatus());
        return existingRental;
    }

    @Override
    @Deprecated
    public Rental update(int id, Rental rental) {
        // This is now safe because the main update method will fetch the managed entity
        // based on the ID set here.
        new Rental.Builder().copy(rental).setId(id).applyTo(rental);
        return update(rental);
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a rental by fetching the entity and using the builder's `applyTo` method.
     */
    @Override
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete rental ID: {}", id);
        return rentalRepository.findByIdAndDeletedFalse(id).map(rental -> {
            Car car = rental.getCar();
            if (car != null && !car.isAvailable() && rental.getStatus() == RentalStatus.ACTIVE) {
                new Car.Builder().copy(car).setAvailable(true).applyTo(car);
                log.info("Car UUID {} marked as available due to deletion of active rental ID {}", car.getUuid(), id);
            }
            new Rental.Builder().copy(rental).setDeleted(true).setStatus(RentalStatus.CANCELLED).applyTo(rental);
            log.info("Rental ID {} successfully marked as deleted.", id);
            return true;
        }).orElse(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental completeRentalByUuid(UUID rentalUuid, double fineAmount) {
        log.info("Completing rental UUID: {}", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) throw new ResourceNotFoundException("Rental not found: " + rentalUuid);
        if (rental.getStatus() != RentalStatus.ACTIVE) throw new IllegalStateException("Only an ACTIVE rental can be completed.");

        Car car = rental.getCar();
        if (car != null) {
            new Car.Builder().copy(car).setAvailable(true).applyTo(car);
            log.info("Car UUID {} marked as available upon rental completion.", car.getUuid());
        }

        new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.COMPLETED)
                .setReturnedDate(LocalDateTime.now())
                .setFine((int) fineAmount)
                .applyTo(rental);

        log.info("Rental UUID {} completed successfully.", rentalUuid);
        return rental;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental cancelRentalByUuid(UUID rentalUuid) {
        log.info("Cancelling rental UUID: {}", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) throw new ResourceNotFoundException("Rental not found: " + rentalUuid);
        if (rental.getStatus() == RentalStatus.COMPLETED) throw new IllegalStateException("Cannot cancel a completed rental.");
        if (rental.getStatus() == RentalStatus.CANCELLED) return rental;

        Car car = rental.getCar();
        if (car != null && !car.isAvailable()) {
            new Car.Builder().copy(car).setAvailable(true).applyTo(car);
            log.info("Car UUID {} marked as available upon rental cancellation.", car.getUuid());
        }

        new Rental.Builder().copy(rental).setStatus(RentalStatus.CANCELLED).applyTo(rental);
        log.info("Rental UUID {} cancelled successfully.", rentalUuid);
        return rental;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rental confirmRentalByUuid(UUID rentalUuid) {
        log.info("Confirming rental UUID: {}", rentalUuid);
        Rental rental = read(rentalUuid);
        if (rental == null) throw new ResourceNotFoundException("Rental not found: " + rentalUuid);
        if (rental.getStatus() == RentalStatus.ACTIVE) return rental;

        Car car = rental.getCar();
        if (car != null && car.isAvailable()) {
            new Car.Builder().copy(car).setAvailable(false).applyTo(car);
            log.info("Car UUID {} marked as unavailable upon rental confirmation.", car.getUuid());
        }

        new Rental.Builder().copy(rental).setStatus(RentalStatus.ACTIVE).applyTo(rental);
        log.info("Rental UUID {} confirmed successfully.", rentalUuid);
        return rental;
    }

    @Override public List<Rental> getAll() { return rentalRepository.findAllByDeletedFalse(); }
    @Override public boolean isCurrentlyRenting(User user) { if(user==null || user.getId()==null) return false; return !rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(user.getId(), RentalStatus.ACTIVE).isEmpty(); }
    @Override public Rental getCurrentRental(User user) { if(user==null || user.getId()==null) return null; return rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(user.getId(), RentalStatus.ACTIVE).stream().findFirst().orElse(null); }
    @Override public boolean existsById(Integer id) { return rentalRepository.existsByIdAndDeletedFalse(id); }
    @Override public List<Rental> getRentalHistoryByUser(User user) { if(user==null || user.getId()==null) return Collections.emptyList(); return rentalRepository.findByUserIdAndDeletedFalse(user.getId()); }
    @Override public List<Rental> findRentalsDueToday() { LocalDateTime start = LocalDate.now().atStartOfDay(); LocalDateTime end = LocalDate.now().atTime(23, 59, 59); return rentalRepository.findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(start, end, RentalStatus.ACTIVE); }
    @Override public List<Rental> findOverdueRentals() { return rentalRepository.findByExpectedReturnDateBeforeAndStatusAndReturnedDateIsNullAndDeletedFalse(LocalDateTime.now(), RentalStatus.ACTIVE); }
    @Override public List<Rental> findRentalsDueOnDate(LocalDate date) { LocalDateTime start = date.atStartOfDay(); LocalDateTime end = date.atTime(23, 59, 59); return rentalRepository.findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(start, end, RentalStatus.ACTIVE); }

    /**
     * Finds all rentals that are currently considered "active".
     * An active rental is defined as having the status ACTIVE, not being soft-deleted,
     * and having no registered return date. This provides a robust and accurate
     * representation of all cars currently in possession of a customer.
     *
     * @return A List of {@link Rental} entities that are currently active.
     *         Returns an empty list if no active rentals are found.
     */
    @Override
    public List<Rental> findActiveRentals() {
        log.debug("Fetching all active rentals.");
        List<Rental> activeRentals = rentalRepository.findByStatusAndReturnedDateIsNullAndDeletedFalse(RentalStatus.ACTIVE);
        log.debug("Found {} active rentals.", activeRentals.size());
        return activeRentals;
    }

    @Override public List<Rental> findByUserIdAndReturnedDateIsNullAndDeletedFalse(Integer userId) { return rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(userId); }

    private void updateCarAvailabilityOnStatusChange(Car car, RentalStatus oldStatus, RentalStatus newStatus) {
        if (car == null) return;
        boolean wasActive = oldStatus == RentalStatus.ACTIVE;
        boolean isNowTerminal = newStatus == RentalStatus.COMPLETED || newStatus == RentalStatus.CANCELLED;

        if (wasActive && isNowTerminal && !car.isAvailable()) {
            new Car.Builder().copy(car).setAvailable(true).applyTo(car);
        } else if (!wasActive && newStatus == RentalStatus.ACTIVE && car.isAvailable()) {
            new Car.Builder().copy(car).setAvailable(false).applyTo(car);
        }
    }
}