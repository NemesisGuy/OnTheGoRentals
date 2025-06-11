/*
package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;
import za.ac.cput.factory.impl.RentalFactory;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IDriverService;
import za.ac.cput.service.IUserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

*/
/**
 * Unit tests for {@link RentalServiceImpl}.
 * Tests rental lifecycle management, including creation, updates, status changes,
 * and interactions with related services and repositories.
 * Assumes entities are modified via Builder pattern returning new instances for updates.
 *
 * Author: Peter Buckingham
 * Date: 2025-05-30
 *//*

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RentalFactory rentalFactory;
    @Mock
    private IUserService userService;
    @Mock
    private IBookingService bookingService;
    @Mock
    private ICarService carService;
    @Mock
    private IDriverService driverService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private User sampleUser;
    private Car sampleCarAvailable;
    private Car sampleCarUnavailable;
    private Booking sampleBookingConfirmed;
    private Rental sampleRentalActive;
    private Driver sampleDriver;
    private UUID commonRentalUuid;

    @BeforeEach
    void setUp() {
        commonRentalUuid = UUID.randomUUID();
        sampleUser = User.builder().id(1).email("test@example.com").firstName("Test").lastName("User").uuid(UUID.randomUUID()).build();
        sampleDriver = new Driver.Builder().setId(1).setUuid(UUID.randomUUID()).setFirstName("Drive").setLastName("Ryder").build();

        sampleCarAvailable = new Car.Builder().setId(1).setUuid(UUID.randomUUID()).setMake("Toyota").setModel("Corolla").setAvailable(true).setDeleted(false).build();
        sampleCarUnavailable = new Car.Builder().setId(2).setUuid(UUID.randomUUID()).setMake("Honda").setModel("Civic").setAvailable(false).setDeleted(false).build();

        sampleBookingConfirmed = new Booking.Builder()
                .setId(1)
                .setUuid(UUID.randomUUID())
                .setUser(sampleUser)
                .setCar(sampleCarAvailable)
                .setStartDate(LocalDateTime.now().plusDays(1))
                .setEndDate(LocalDateTime.now().plusDays(3))
                .setStatus(BookingStatus.CONFIRMED)
                .build();

        Car carForActiveRental = new Car.Builder().copy(sampleCarAvailable).setAvailable(false).build();
        sampleRentalActive = new Rental.Builder()
                .setId(1)
                .setUuid(commonRentalUuid)
                .setUser(sampleUser)
                .setCar(carForActiveRental)
                .setIssuedDate(LocalDateTime.now().minusDays(1))
                .setExpectedReturnDate(LocalDateTime.now().plusDays(2))
                .setStatus(RentalStatus.ACTIVE)
                .setDeleted(false)
                .setCreatedAt(LocalDateTime.now().minusDays(1))
                .setUpdatedAt(LocalDateTime.now().minusHours(5))
                .build();
    }

    // --- create Tests ---
    @Test
    void create_shouldCreateRentalAndMakeCarUnavailable_whenValid() {
        Rental rentalDataFromController = new Rental.Builder()
                .setUser(sampleUser)
                .setCar(sampleCarAvailable)
                .setIssuedDate(LocalDateTime.now())
                .setExpectedReturnDate(LocalDateTime.now().plusDays(2))
                .build();

        Car carMadeUnavailable = new Car.Builder().copy(sampleCarAvailable).setAvailable(false).build();
        Rental rentalPreparedByFactory = new Rental.Builder() // Simulate what factory does
                .copy(rentalDataFromController)
                .setCar(carMadeUnavailable) // Service updates car ref *after* factory in current impl
                .setUuid(UUID.randomUUID())
                .setStatus(RentalStatus.ACTIVE)
                .setDeleted(false)
                .setCreatedAt(rentalDataFromController.getIssuedDate())
                .setUpdatedAt(rentalDataFromController.getIssuedDate())
                .setId(5)
                .build();
        // The rentalService.create method actually sets car to unavailable *after* factory call
        // So, the object passed to rentalRepository.save() will have the car made unavailable
        Rental finalSavedRentalState = new Rental.Builder().copy(rentalPreparedByFactory).setCar(carMadeUnavailable).build();


        when(carRepository.existsByIdAndAvailableTrueAndDeletedFalse(sampleCarAvailable.getId())).thenReturn(true);
        when(rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(sampleUser.getId(), RentalStatus.ACTIVE))
                .thenReturn(Collections.emptyList());
        when(rentalFactory.create(any(Rental.class))).thenReturn(rentalPreparedByFactory); // Factory returns its version
        when(carRepository.findById(sampleCarAvailable.getId())).thenReturn(Optional.of(sampleCarAvailable)); // For fetching car to update
        when(carRepository.save(argThat(car -> !car.isAvailable()))).thenReturn(carMadeUnavailable); // Car save
        when(rentalRepository.save(any(Rental.class))).thenReturn(finalSavedRentalState); // Rental save returns final state

        Rental createdRental = rentalService.create(rentalDataFromController);

        assertNotNull(createdRental);
        assertEquals(RentalStatus.ACTIVE, createdRental.getStatus());
        assertNotNull(createdRental.getUuid());
        assertNotNull(createdRental.getCar());
        assertFalse(createdRental.getCar().isAvailable(), "Car within the returned Rental entity should be unavailable");

        verify(carRepository).save(argThat(car -> !car.isAvailable()));
        verify(rentalRepository).save(argThat(rental -> rental.getCar() != null && !rental.getCar().isAvailable()));
    }

    @Test
    void create_shouldThrowCarNotAvailableException_whenCarIsUnavailable() {
        Rental rentalToCreateInput = new Rental.Builder().setUser(sampleUser).setCar(sampleCarUnavailable).build();
        when(carRepository.existsByIdAndAvailableTrueAndDeletedFalse(sampleCarUnavailable.getId())).thenReturn(false);

        assertThrows(CarNotAvailableException.class, () -> rentalService.create(rentalToCreateInput));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void create_shouldThrowUserCantRentMoreThanOneCarException_whenUserIsCurrentlyRenting() {
        Rental rentalToCreateInput = new Rental.Builder().setUser(sampleUser).setCar(sampleCarAvailable).build();
        when(carRepository.existsByIdAndAvailableTrueAndDeletedFalse(sampleCarAvailable.getId())).thenReturn(true);
        when(rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(sampleUser.getId(), RentalStatus.ACTIVE))
                .thenReturn(List.of(sampleRentalActive));

        assertThrows(UserCantRentMoreThanOneCarException.class, () -> rentalService.create(rentalToCreateInput));
    }


    // --- createRentalFromBooking Tests ---
    @Test
    void createRentalFromBooking_shouldSucceedAndSetStatusesAndCarUnavailable() {
        UUID issuerId = UUID.randomUUID(); // Simulate issuer ID, e.g., admin or system
        LocalDateTime pickupTime = LocalDateTime.now();
        UUID driverUuidForRental = (sampleDriver != null) ? sampleDriver.getUuid() : null;

        Car carFromBooking = sampleBookingConfirmed.getCar(); // Initially available
        Car carMadeUnavailable = new Car.Builder().copy(carFromBooking).setAvailable(false).build();

        Rental finalRentalState = new Rental.Builder()
                .setUser(sampleUser)
                .setCar(carMadeUnavailable) // Rental should contain the car in its NEW (unavailable) state
                .setDriver(sampleDriver)
                .setIssuedDate(pickupTime)
                .setExpectedReturnDate(sampleBookingConfirmed.getEndDate())
                .setStatus(RentalStatus.ACTIVE)
                .setIssuer(issuerId)
                .setDeleted(false)
                .setUuid(UUID.randomUUID()).setId(10).setCreatedAt(pickupTime).setUpdatedAt(pickupTime)
                .build();

        Booking bookingAfterUpdate = new Booking.Builder().copy(sampleBookingConfirmed).setStatus(BookingStatus.RENTAL_INITIATED).build();

        when(bookingService.read(sampleBookingConfirmed.getUuid())).thenReturn(sampleBookingConfirmed);
        when(carService.read(carFromBooking.getUuid())).thenReturn(carFromBooking); // Service returns current state (available)
        if (driverUuidForRental != null) when(driverService.read(driverUuidForRental)).thenReturn(sampleDriver);
        when(carService.update(argThat(c -> !c.isAvailable() && c.getId() == carFromBooking.getId()))).thenReturn(carMadeUnavailable);
        when(rentalRepository.save(any(Rental.class))).thenReturn(finalRentalState);
        when(bookingService.update(any(Booking.class))).thenReturn(bookingAfterUpdate);

        Rental createdRental = rentalService.createRentalFromBooking(sampleBookingConfirmed.getUuid(), issuerId, driverUuidForRental, pickupTime);

        assertNotNull(createdRental);
        assertEquals(RentalStatus.ACTIVE, createdRental.getStatus());
        assertNotNull(createdRental.getCar());
        assertFalse(createdRental.getCar().isAvailable());
        assertEquals(sampleDriver, createdRental.getDriver());

        verify(carService).update(argThat(car -> !car.isAvailable() && car.getId() == carFromBooking.getId()));
        verify(bookingService).update(argThat(booking -> booking.getStatus() == BookingStatus.RENTAL_INITIATED));
        verify(rentalRepository).save(argThat(r -> !r.getCar().isAvailable()));
    }
    // ... (createRentalFromBooking failure tests) ...

    // --- update Tests ---
    @Test
    void update_shouldUpdateRentalAndMakeCarAvailable_whenCarIsReturned() {
        Car carInRental = new Car.Builder().copy(sampleCarUnavailable).setAvailable(false).build(); // Car is unavailable
        Rental existingRental = new Rental.Builder()
                .copy(sampleRentalActive)
                .setId(1).setUuid(commonRentalUuid)
                .setCar(carInRental).setStatus(RentalStatus.ACTIVE)
                .build();

        Car carMadeAvailable = new Car.Builder().copy(carInRental).setAvailable(true).build();
        Rental rentalWithUpdates = new Rental.Builder().copy(existingRental)
                .setReturnedDate(LocalDateTime.now())
                .setStatus(RentalStatus.COMPLETED)
                .setCar(carMadeAvailable) // The new state of rental includes the car as available
                .build();

        when(rentalRepository.findByIdAndDeletedFalse(existingRental.getId())).thenReturn(Optional.of(existingRental));
        when(carService.read(carInRental.getUuid())).thenReturn(carInRental); // carService returns current state
        when(carRepository.save(argThat(Car::isAvailable))).thenReturn(carMadeAvailable);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rentalWithUpdates);


        Rental updatedRental = rentalService.update(rentalWithUpdates);

        assertNotNull(updatedRental);
        assertEquals(RentalStatus.COMPLETED, updatedRental.getStatus());
        assertNotNull(updatedRental.getReturnedDate());
        assertNotNull(updatedRental.getCar());
        assertTrue(updatedRental.getCar().isAvailable());
        verify(carRepository).save(argThat(c -> c.getId() == carInRental.getId() && c.isAvailable()));
        verify(rentalRepository).save(argThat(r -> r.getCar().isAvailable()));
    }
    // ... (update rental not found test) ...


    // --- delete Tests ---
    @Test
    void delete_shouldSoftDeleteAndMakeCarAvailable_ifRentalWasActive() {
        Car carForActiveRental = new Car.Builder().copy(sampleCarAvailable).setAvailable(false).build();
        Rental activeRentalToDelete = new Rental.Builder().copy(sampleRentalActive)
                .setCar(carForActiveRental).setStatus(RentalStatus.ACTIVE).build();
        activeRentalToDelete = new Rental.Builder().copy(activeRentalToDelete).setId(1).build();


        Car carMadeAvailable = new Car.Builder().copy(carForActiveRental).setAvailable(true).build();
        Rental finalStateAfterDelete = new Rental.Builder().copy(activeRentalToDelete)
                .setDeleted(true).setStatus(RentalStatus.CANCELLED)
                .setCar(carMadeAvailable).build();

        when(rentalRepository.findByIdAndDeletedFalse(activeRentalToDelete.getId())).thenReturn(Optional.of(activeRentalToDelete));
        when(carRepository.save(argThat(Car::isAvailable))).thenReturn(carMadeAvailable);
        when(rentalRepository.save(any(Rental.class))).thenReturn(finalStateAfterDelete);

        boolean result = rentalService.delete(activeRentalToDelete.getId());

        assertTrue(result);
        verify(rentalRepository).save(argThat(r -> r.isDeleted() && r.getStatus() == RentalStatus.CANCELLED && r.getCar().isAvailable()));
        verify(carRepository).save(argThat(c -> c.getId() == carForActiveRental.getId() && c.isAvailable()));
    }
    // ... (other delete tests) ...

    // --- Status Change Method Tests (confirm, cancel, complete) ---
    // (Similar pattern: mock read, mock saves, assert returned rental's car state)
    @Test
    void confirmRentalByUuid_shouldConfirmAndMakeCarUnavailable() {
        Car carInitiallyAvailable = new Car.Builder().copy(sampleCarAvailable).setAvailable(true).build();
        Rental rentalToConfirm = new Rental.Builder().copy(sampleRentalActive)
                .setCar(carInitiallyAvailable)
                .setStatus(RentalStatus.ACTIVE) // Assuming confirming means ensuring it's active
                .setUuid(commonRentalUuid).build();

        Car carMadeUnavailable = new Car.Builder().copy(carInitiallyAvailable).setAvailable(false).build();
        Rental rentalAfterConfirm = new Rental.Builder().copy(rentalToConfirm)
                .setCar(carMadeUnavailable) // Rental should now have the car in its 'unavailable' state
                .setStatus(RentalStatus.ACTIVE)
                .build();
        rentalAfterConfirm = new Rental.Builder().copy(rentalAfterConfirm).setIssuedDate(rentalToConfirm.getIssuedDate()).build(); // Keep original or set to now()

        when(rentalRepository.findByUuidAndDeletedFalse(commonRentalUuid)).thenReturn(Optional.of(rentalToConfirm));
        when(carRepository.save(argThat(c -> !c.isAvailable()))).thenReturn(carMadeUnavailable);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rentalAfterConfirm);

        Rental confirmedRental = rentalService.confirmRentalByUuid(commonRentalUuid);

        assertEquals(RentalStatus.ACTIVE, confirmedRental.getStatus());
        assertNotNull(confirmedRental.getCar());
        assertFalse(confirmedRental.getCar().isAvailable());
    }

    @Test
    void cancelRentalByUuid_shouldCancelAndMakeCarAvailable() {
        Car carInitiallyUnavailable = new Car.Builder().copy(sampleCarAvailable).setAvailable(false).build();
        Rental rentalToCancel = new Rental.Builder().copy(sampleRentalActive)
                .setCar(carInitiallyUnavailable)
                .setStatus(RentalStatus.ACTIVE).setUuid(commonRentalUuid).build();

        Car carMadeAvailable = new Car.Builder().copy(carInitiallyUnavailable).setAvailable(true).build();
        Rental rentalAfterCancel = new Rental.Builder().copy(rentalToCancel)
                .setStatus(RentalStatus.CANCELLED).setCar(carMadeAvailable).build();

        when(rentalRepository.findByUuidAndDeletedFalse(commonRentalUuid)).thenReturn(Optional.of(rentalToCancel));
        when(carRepository.save(argThat(Car::isAvailable))).thenReturn(carMadeAvailable);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rentalAfterCancel);

        Rental cancelledRental = rentalService.cancelRentalByUuid(commonRentalUuid);

        assertEquals(RentalStatus.CANCELLED, cancelledRental.getStatus());
        assertNotNull(cancelledRental.getCar());
        assertTrue(cancelledRental.getCar().isAvailable());
    }

    @Test
    void completeRentalByUuid_shouldCompleteAndMakeCarAvailable() {
        Car carInitiallyUnavailable = new Car.Builder().copy(sampleCarAvailable).setAvailable(false).build();
        Rental rentalToComplete = new Rental.Builder().copy(sampleRentalActive)
                .setCar(carInitiallyUnavailable)
                .setStatus(RentalStatus.ACTIVE).setUuid(commonRentalUuid).build();
        double fineAmount = 50.0;

        Car carMadeAvailable = new Car.Builder().copy(carInitiallyUnavailable).setAvailable(true).build();
        LocalDateTime returnTime = LocalDateTime.now(); // Capture before mocking
        Rental rentalAfterComplete = new Rental.Builder().copy(rentalToComplete)
                .setStatus(RentalStatus.COMPLETED)
                .setReturnedDate(returnTime) // This is what the service will set
                .setFine((int) fineAmount)
                .setCar(carMadeAvailable).build();

        when(rentalRepository.findByUuidAndDeletedFalse(commonRentalUuid)).thenReturn(Optional.of(rentalToComplete));
        when(carRepository.save(argThat(Car::isAvailable))).thenReturn(carMadeAvailable);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            // Simulate the service setting the returnDate and fine correctly
            Rental arg = invocation.getArgument(0);
            return new Rental.Builder().copy(arg)
                    .setReturnedDate(returnTime) // Ensure this matches
                    .setFine((int)fineAmount)
                    .build();
        });


        Rental completedRental = rentalService.completeRentalByUuid(commonRentalUuid, fineAmount);

        assertEquals(RentalStatus.COMPLETED, completedRental.getStatus());
        assertNotNull(completedRental.getReturnedDate());
        // Allow for minor differences in nanoseconds for 'now()'
        assertTrue(completedRental.getReturnedDate().isAfter(returnTime.minusSeconds(1)) &&
                completedRental.getReturnedDate().isBefore(returnTime.plusSeconds(1)));
        assertEquals((int) fineAmount, completedRental.getFine());
        assertNotNull(completedRental.getCar());
        assertTrue(completedRental.getCar().isAvailable());
    }

    // --- Tests for findRentalsDueToday, findOverdueRentals (as before) ---
    // (These tests mostly verify that the correct repository methods are called)
    @Test
    void findRentalsDueToday_shouldCallRepositoryWithCorrectDateRangeAndStatus() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);
        Rental rentalDueToday = new Rental.Builder().copy(sampleRentalActive)
                .setExpectedReturnDate(today.atTime(17,0)).build();

        when(rentalRepository.findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(
                eq(startOfDay), eq(endOfDay), eq(RentalStatus.ACTIVE)))
                .thenReturn(List.of(rentalDueToday));

        List<Rental> result = rentalService.findRentalsDueToday();
        assertFalse(result.isEmpty());
        assertEquals(rentalDueToday, result.get(0));
        verify(rentalRepository).findByExpectedReturnDateBetweenAndStatusAndReturnedDateIsNullAndDeletedFalse(startOfDay, endOfDay, RentalStatus.ACTIVE);
    }

    @Test
    void findOverdueRentals_shouldCallRepositoryWithCorrectDateAndStatus() {
        Rental overdueRental = new Rental.Builder().copy(sampleRentalActive)
                .setExpectedReturnDate(LocalDateTime.now().minusDays(1)).build();

        when(rentalRepository.findByExpectedReturnDateBeforeAndStatusAndReturnedDateIsNullAndDeletedFalse(
                any(LocalDateTime.class), eq(RentalStatus.ACTIVE)))
                .thenReturn(List.of(overdueRental));

        List<Rental> result = rentalService.findOverdueRentals();
        assertFalse(result.isEmpty());
        assertEquals(overdueRental, result.get(0));
        verify(rentalRepository).findByExpectedReturnDateBeforeAndStatusAndReturnedDateIsNullAndDeletedFalse(any(LocalDateTime.class), eq(RentalStatus.ACTIVE));
    }
    /// ////////////////////////

    // In RentalServiceImplTest.java
    @Test
    void delete_shouldSoftDeleteRentalAndMakeCarAvailable_ifRentalWasActive() {
        Car carForRental = new Car.Builder().copy(sampleCarAvailable).setAvailable(false).build();
        Rental activeRentalToDelete = new Rental.Builder().copy(sampleRentalActive)
                .setCar(carForRental)
                .setStatus(RentalStatus.ACTIVE)
                .setId(1) // Ensure ID is set
                .build();

        Car carMadeAvailable = new Car.Builder().copy(carForRental).setAvailable(true).build();
        Rental finalStateAfterDelete = new Rental.Builder().copy(activeRentalToDelete)
                .setDeleted(true).setStatus(RentalStatus.CANCELLED)
                .setCar(carMadeAvailable).build();

        // Only this stub is needed for the find operation in delete()
        when(rentalRepository.findByIdAndDeletedFalse(activeRentalToDelete.getId())).thenReturn(Optional.of(activeRentalToDelete));
        // when(rentalRepository.existsByIdAndDeletedFalse(sampleRentalActive.getId())).thenReturn(true); // REMOVE THIS
        when(carRepository.save(argThat(Car::isAvailable))).thenReturn(carMadeAvailable);
        when(rentalRepository.save(any(Rental.class))).thenReturn(finalStateAfterDelete);

        boolean result = rentalService.delete(activeRentalToDelete.getId());

        assertTrue(result);
        // ... rest of verifies ...
    }

    // ... (Other existing tests: read, getAll, isCurrentlyRenting, getCurrentRental, existsById, getRentalHistoryByUser)
    // Ensure they use .build() correctly for any entities created with builders.
}*/
