package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.InvalidDateRangeException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.service.ICarService;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Use lenient for now to avoid unnecessary stubbing issues during refactor
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ICarService carService;
    @Mock
    private IUserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User sampleUser;
    private Car sampleCar;
    private Booking sampleBooking; // This is CONFIRMED by default
    private UUID userUuid;
    private UUID carUuid;
    private UUID bookingUuid;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        carUuid = UUID.randomUUID();
        bookingUuid = UUID.randomUUID();
        // Ensure consistent start/end times for tests
        startTime = LocalDateTime.now().plusDays(1).withNano(0); // Remove nanos for easier comparison
        endTime = LocalDateTime.now().plusDays(3).withNano(0);   // Remove nanos

        sampleUser = User.builder().id(1).uuid(userUuid).email("user@example.com").build();
        sampleCar = new Car.Builder().setId(1).setUuid(carUuid).setMake("Toyota").setModel("Corolla").build();

        sampleBooking = new Booking.Builder()
                .setId(1)
                .setUuid(bookingUuid)
                .setUser(sampleUser)
                .setCar(sampleCar)
                .setStartDate(startTime)
                .setEndDate(endTime)
                .setStatus(BookingStatus.CONFIRMED)
                .setDeleted(false)
                .setCreatedAt(LocalDateTime.now().minusHours(1))
                .setUpdatedAt(LocalDateTime.now().minusMinutes(30))
                .build();
    }

    // --- create(Booking bookingDetails) Tests ---
    @Test
    void create_shouldSucceed_whenValid() {
        Booking bookingDetails = new Booking.Builder()
                .setUser(sampleUser)
                .setCar(sampleCar)
                .setStartDate(startTime)
                .setEndDate(endTime)
                .build();
        Booking savedBookingMock = new Booking.Builder().copy(bookingDetails).setId(2).setUuid(UUID.randomUUID()).setStatus(BookingStatus.CONFIRMED).build();

        when(userService.read(userUuid)).thenReturn(sampleUser);
        when(carService.read(carUuid)).thenReturn(sampleCar);
        when(bookingRepository.findOverlappingBookings(eq(sampleCar.getId()), eq(BookingStatus.CONFIRMED), eq(startTime), eq(endTime)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBookingMock);

        Booking created = bookingService.create(bookingDetails);

        assertNotNull(created);
        assertEquals(BookingStatus.CONFIRMED, created.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_shouldThrowIllegalArgument_whenDetailsIncomplete() {
        Booking incompleteBooking = new Booking.Builder().setUser(sampleUser).setStartDate(startTime).build();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> bookingService.create(incompleteBooking));
        assertEquals("User, Car, Start Date, and End Date must be provided for booking.", ex.getMessage());
    }

    @Test
    void create_shouldThrowIllegalArgument_whenUserOrCarUuidIsNull() {
        User userWithNullUuid = User.builder().id(1).uuid(null).build();
        Booking bookingWithNullUserUuid = new Booking.Builder()
                .setUser(userWithNullUuid).setCar(sampleCar).setStartDate(startTime).setEndDate(endTime).build();
        Exception ex1 = assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingWithNullUserUuid));
        assertEquals("User UUID and Car UUID must be valid for booking creation.", ex1.getMessage());

        Car carWithNullUuid = new Car.Builder().setId(1).setUuid(null).build();
        Booking bookingWithNullCarUuid = new Booking.Builder()
                .setUser(sampleUser).setCar(carWithNullUuid).setStartDate(startTime).setEndDate(endTime).build();
        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingWithNullCarUuid));
        assertEquals("User UUID and Car UUID must be valid for booking creation.", ex2.getMessage());
    }


    @Test
    void create_shouldThrowInvalidDateRange_whenEndDateNotAfterStartDate() {
        Booking bookingDetails = new Booking.Builder()
                .setUser(sampleUser).setCar(sampleCar).setStartDate(startTime).setEndDate(startTime.minusHours(1)).build();
        Exception ex = assertThrows(InvalidDateRangeException.class, () -> bookingService.create(bookingDetails));
        assertEquals("Booking end date must be after the booking start date.", ex.getMessage());
    }

    @Test
    void create_shouldThrowInvalidDateRange_whenDurationTooShort() {
        Booking bookingDetails = new Booking.Builder()
                .setUser(sampleUser).setCar(sampleCar)
                .setStartDate(startTime).setEndDate(startTime.plusMinutes(30)).build();
        Exception ex = assertThrows(InvalidDateRangeException.class, () -> bookingService.create(bookingDetails));
        assertEquals("Booking duration must be at least 1 hour(s).", ex.getMessage());
    }

    @Test
    void create_shouldThrowResourceNotFound_whenCarNotFound() {
        Booking bookingDetails = new Booking.Builder().setUser(sampleUser).setCar(sampleCar).setStartDate(startTime).setEndDate(endTime).build();
        when(userService.read(userUuid)).thenReturn(sampleUser); // This should be fine now
        when(carService.read(carUuid)).thenReturn(null);
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> bookingService.create(bookingDetails));
        assertTrue(ex.getMessage().contains("Car with UUID " + carUuid + " not found."));
    }

    @Test
    void create_shouldThrowResourceNotFound_whenUserNotFound() {
        Booking bookingDetails = new Booking.Builder().setUser(sampleUser).setCar(sampleCar).setStartDate(startTime).setEndDate(endTime).build();
        when(carService.read(carUuid)).thenReturn(sampleCar); // Ensure car is found
        when(userService.read(userUuid)).thenReturn(null);   // User not found
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> bookingService.create(bookingDetails));
        assertTrue(ex.getMessage().contains("User with UUID " + userUuid + " not found."), "Exception message should indicate user not found.");
    }

    @Test
    void create_shouldThrowCarNotAvailable_whenCarIsDoubleBooked() {
        Booking bookingDetails = new Booking.Builder().setUser(sampleUser).setCar(sampleCar).setStartDate(startTime).setEndDate(endTime).build();
        when(userService.read(userUuid)).thenReturn(sampleUser);
        when(carService.read(carUuid)).thenReturn(sampleCar);
        when(bookingRepository.findOverlappingBookings(eq(sampleCar.getId()), eq(BookingStatus.CONFIRMED), eq(startTime), eq(endTime)))
                .thenReturn(List.of(sampleBooking));

        Exception ex = assertThrows(CarNotAvailableException.class, () -> bookingService.create(bookingDetails));
        assertTrue(ex.getMessage().contains("is not available for the selected dates."));
    }

    // --- createBooking(Booking booking) Simplified ---
    @Test
    void createBookingSimplified_shouldSucceed() {
        Booking bookingDetails = new Booking.Builder()
                .setUser(sampleUser).setCar(sampleCar).setStartDate(startTime).setEndDate(endTime).build();
        Booking savedBooking = new Booking.Builder().copy(bookingDetails).setId(3).setUuid(UUID.randomUUID()).setStatus(BookingStatus.CONFIRMED).build();

        when(userService.read(userUuid)).thenReturn(sampleUser);
        when(carService.read(carUuid)).thenReturn(sampleCar);
        when(bookingRepository.findOverlappingBookings(eq(sampleCar.getId()), eq(BookingStatus.CONFIRMED), eq(startTime), eq(endTime)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking created = bookingService.createBooking(bookingDetails);
        assertNotNull(created);
        assertEquals(BookingStatus.CONFIRMED, created.getStatus());
    }

    // --- confirmBooking Tests ---
    @Test
    void confirmBooking_shouldConfirm_whenStatusAllows() {
        Booking pendingBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.CONFIRMED).build(); // Or PENDING_CONFIRMATION
        Booking confirmedBookingResult = new Booking.Builder().copy(pendingBooking).setStatus(BookingStatus.CONFIRMED).build();

        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(confirmedBookingResult);

        Booking result = bookingService.confirmBooking(1);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository).save(argThat(b -> b.getStatus() == BookingStatus.CONFIRMED));
    }

    @Test
    void confirmBooking_shouldThrowResourceNotFound_whenBookingNotFound() {
        when(bookingRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.confirmBooking(99));
    }

    @Test
    void confirmBooking_shouldThrowIllegalState_whenStatusIsRentalInitiated() {
        Booking rentalInitiatedBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.RENTAL_INITIATED).build();
        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(rentalInitiatedBooking));

        Exception ex = assertThrows(IllegalStateException.class, () -> bookingService.confirmBooking(1));
        assertEquals("Booking cannot be confirmed from status: " + BookingStatus.RENTAL_INITIATED, ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // --- cancelBooking Tests ---
    @Test
    void cancelBooking_shouldSucceedAndSetUserCancelled_whenStatusIsConfirmed() {
        // sampleBooking is CONFIRMED
        Booking cancelledBookingResult = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.USER_CANCELLED).build();

        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(cancelledBookingResult);

        Booking result = bookingService.cancelBooking(1);
        assertEquals(BookingStatus.USER_CANCELLED, result.getStatus());
        verify(bookingRepository).save(argThat(b -> b.getStatus() == BookingStatus.USER_CANCELLED));
    }

    @Test
    void cancelBooking_shouldThrowIllegalState_whenStatusIsRentalInitiated() {
        Booking rentalInitiatedBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.RENTAL_INITIATED).build();
        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(rentalInitiatedBooking));

        Exception ex = assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(1));
        assertEquals("Booking cannot be cancelled from status: " + BookingStatus.RENTAL_INITIATED, ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrowIllegalState_whenAlreadyUserCancelled() {
        Booking alreadyCancelledBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.USER_CANCELLED).build();
        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(alreadyCancelledBooking));

        Exception ex = assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(1));
        assertEquals("Booking cannot be cancelled from status: " + BookingStatus.USER_CANCELLED, ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrowIllegalState_whenStatusIsNoShow() {
        Booking noShowBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.NO_SHOW).build();
        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(noShowBooking));

        Exception ex = assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(1));
        assertEquals("Booking cannot be cancelled from status: " + BookingStatus.NO_SHOW, ex.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // --- Read Tests ---
    @Test
    void readById_shouldReturnBooking_whenFound() {
        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleBooking));
        Booking found = bookingService.read(1);
        assertNotNull(found);
        assertEquals(sampleBooking.getUuid(), found.getUuid());
    }

    @Test
    void readByUuid_shouldReturnBooking_whenFound() {
        when(bookingRepository.findByUuidAndDeletedFalse(bookingUuid)).thenReturn(Optional.of(sampleBooking));
        Booking found = bookingService.read(bookingUuid);
        assertNotNull(found);
        assertEquals(sampleBooking.getId(), found.getId());
    }

    // --- Update Tests ---
    @Test
    void update_shouldSucceed_whenValidAndNoDateOrCarChange() {
        Booking updates = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.ADMIN_CANCELLED).build();
        Booking expectedSavedBooking = new Booking.Builder()
                .copy(updates)
                .setId(sampleBooking.getId()).setUuid(sampleBooking.getUuid())
                .setUser(sampleBooking.getUser()).setCreatedAt(sampleBooking.getCreatedAt())
                .setDeleted(sampleBooking.isDeleted()).build();

        when(bookingRepository.findByIdAndDeletedFalse(sampleBooking.getId())).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(expectedSavedBooking);

        Booking updated = bookingService.update(updates);
        assertNotNull(updated);
        assertEquals(BookingStatus.ADMIN_CANCELLED, updated.getStatus());
        verify(bookingRepository).save(argThat(b ->
                b.getStatus() == BookingStatus.ADMIN_CANCELLED &&
                        b.getId() == sampleBooking.getId() &&
                        b.getUuid().equals(sampleBooking.getUuid())
        ));
    }

    @Test
    void update_shouldSucceed_whenDatesChangedAndCarAvailable() {
        LocalDateTime newStartTime = startTime.plusDays(1).withNano(0);
        LocalDateTime newEndTime = endTime.plusDays(1).withNano(0);
        Booking bookingWithUpdates = new Booking.Builder().copy(sampleBooking)
                .setStartDate(newStartTime).setEndDate(newEndTime).build();
        Booking expectedEntityToSave = new Booking.Builder().copy(bookingWithUpdates)
                .setId(sampleBooking.getId()).setUuid(sampleBooking.getUuid())
                .setUser(sampleBooking.getUser()).setCar(sampleCar) // Assuming car doesn't change, or is re-verified
                .setCreatedAt(sampleBooking.getCreatedAt()).setDeleted(sampleBooking.isDeleted()).build();

        when(bookingRepository.findByIdAndDeletedFalse(sampleBooking.getId())).thenReturn(Optional.of(sampleBooking));
        when(carService.read(sampleCar.getUuid())).thenReturn(sampleCar);
        lenient().when(bookingRepository.findOverlappingBookings(eq(sampleCar.getId()), eq(BookingStatus.CONFIRMED), eq(newStartTime), eq(newEndTime)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(expectedEntityToSave);

        Booking updated = bookingService.update(bookingWithUpdates);
        assertNotNull(updated);
        assertEquals(newStartTime, updated.getStartDate());
        assertEquals(sampleBooking.getUuid(), updated.getUuid());
    }

    @Test
    void update_shouldThrowResourceNotFound_whenBookingNotFound() {
        Booking updates = new Booking.Builder().copy(sampleBooking).setId(99).build();
        when(bookingRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.update(updates));
    }

    @Test
    void update_shouldThrowIllegalArgument_whenIdIsZero() {
        Booking updates = new Booking.Builder().copy(sampleBooking).setId(0).build();
        assertThrows(IllegalArgumentException.class, () -> bookingService.update(updates));
    }

    @Test
    void update_shouldThrowCarNotAvailable_whenDatesChangedAndCarDoubleBooked() {
        LocalDateTime newStartTime = startTime.plusDays(1).withNano(0);
        LocalDateTime newEndTime = endTime.plusDays(1).withNano(0);
        Booking updates = new Booking.Builder().copy(sampleBooking)
                .setStartDate(newStartTime).setEndDate(newEndTime).build();
        Booking overlappingExistingBooking = new Booking.Builder().copy(sampleBooking).setId(55).setCar(sampleCar).build();

        when(bookingRepository.findByIdAndDeletedFalse(sampleBooking.getId())).thenReturn(Optional.of(sampleBooking));
        when(carService.read(sampleCar.getUuid())).thenReturn(sampleCar);
        lenient().when(bookingRepository.findOverlappingBookings(eq(sampleCar.getId()), eq(BookingStatus.CONFIRMED), eq(newStartTime), eq(newEndTime)))
                .thenReturn(List.of(overlappingExistingBooking));

        assertThrows(CarNotAvailableException.class, () -> bookingService.update(updates));
    }

  /*  // --- Delete Tests ---
    @Test
    void delete_shouldSoftDelete_whenBookingExistsAndCancellable() {
        // Make sure sampleBooking is in a state that allows deletion (e.g., CONFIRMED)
        Booking bookingToDelete = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.CONFIRMED).build();
        Booking deletedStateBooking = new Booking.Builder().copy(bookingToDelete)
                .setDeleted(true).setStatus(BookingStatus.ADMIN_CANCELLED).build();

        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(bookingToDelete));
        when(bookingRepository.save(any(Booking.class))).thenReturn(deletedStateBooking);

        boolean result = bookingService.delete(1);
        assertTrue(result);
        verify(bookingRepository).save(argThat(b -> b.isDeleted() && b.getStatus() == BookingStatus.ADMIN_CANCELLED));
    }
*/
    @Test
    void delete_shouldThrowIllegalState_whenBookingIsRentalInitiated() {
        Booking rentalInitiatedBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.RENTAL_INITIATED).build();
        when(bookingRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(rentalInitiatedBooking));

        assertThrows(IllegalStateException.class, () -> bookingService.delete(1));
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // --- getAll, getUserBookings, findBookingsForCollectionToday ---
    @Test
    void getAll_shouldReturnList() {
        when(bookingRepository.findByDeletedFalse()).thenReturn(List.of(sampleBooking));
        List<Booking> result = bookingService.getAll();
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsForCollectionToday_shouldCallRepository() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);
        when(bookingRepository.findByStatusAndStartDateBetweenAndDeletedFalse(BookingStatus.CONFIRMED, startOfDay, endOfDay))
                .thenReturn(List.of(sampleBooking));

        List<Booking> result = bookingService.findBookingsForCollectionToday();
        assertFalse(result.isEmpty());
        verify(bookingRepository).findByStatusAndStartDateBetweenAndDeletedFalse(BookingStatus.CONFIRMED, startOfDay, endOfDay);
    }
}