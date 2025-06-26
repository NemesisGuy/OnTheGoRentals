package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IDriverService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private ICarService carService;
    @Mock
    private IBookingService bookingService;
    @Mock
    private IUserService userService;
    @Mock
    private IDriverService driverService;
    @Mock
    private RentalFactory rentalFactory;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private User sampleUser;
    private Car sampleCar;
    private Booking sampleBooking;
    private Rental sampleRental;
    private Driver sampleDriver;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1).uuid(UUID.randomUUID()).build();
        sampleCar = new Car.Builder().setId(1).setUuid(UUID.randomUUID()).setAvailable(true).build();
        sampleDriver = new Driver.Builder().setId(1).setUuid(UUID.randomUUID()).build();
        sampleBooking = new Booking.Builder()
                .setUuid(UUID.randomUUID())
                .setUser(sampleUser)
                .setCar(sampleCar)
                .setEndDate(LocalDateTime.now().plusDays(5))
                .setStatus(BookingStatus.CONFIRMED)
                .build();
        sampleRental = new Rental.Builder()
                .setId(1)
                .setUuid(UUID.randomUUID())
                .setUser(sampleUser)
                .setCar(sampleCar)
                .setStatus(RentalStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should create rental and make car unavailable")
    void create_WithValidData_ShouldSucceed() {
        // Arrange
        when(userService.read(any(UUID.class))).thenReturn(sampleUser);
        when(carService.read(any(UUID.class))).thenReturn(sampleCar);
        when(rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(anyInt(), any(RentalStatus.class)))
                .thenReturn(Collections.emptyList());
        when(rentalRepository.save(any(Rental.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Rental createdRental = rentalService.create(sampleRental);

        // Assert
        assertNotNull(createdRental);
        assertEquals(RentalStatus.ACTIVE, createdRental.getStatus());
        verify(carRepository, times(1)).save(argThat(car -> !car.isAvailable()));
    }

    @Test
    @DisplayName("Should throw CarNotAvailableException when car is not available")
    void create_WithUnavailableCar_ShouldThrowException() {
        // Arrange

        sampleCar= new Car.Builder().setAvailable(false).build(); // Set car as unavailable
        when(userService.read(any(UUID.class))).thenReturn(sampleUser);
        when(carService.read(any(UUID.class))).thenReturn(sampleCar);

        // Act & Assert
        assertThrows(CarNotAvailableException.class, () -> rentalService.create(sampleRental));
        verify(rentalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserCantRentMoreThanOneCarException when user is already renting")
    void create_WhenUserIsAlreadyRenting_ShouldThrowException() {
        // Arrange
        when(userService.read(any(UUID.class))).thenReturn(sampleUser);
        when(carService.read(any(UUID.class))).thenReturn(sampleCar);
        when(rentalRepository.findByUserIdAndStatusAndReturnedDateIsNullAndDeletedFalse(anyInt(), any(RentalStatus.class)))
                .thenReturn(List.of(new Rental())); // User is already renting

        // Act & Assert
        assertThrows(UserCantRentMoreThanOneCarException.class, () -> rentalService.create(sampleRental));
    }

    @Test
    @DisplayName("Should create a rental from a confirmed booking successfully")
    void createRentalFromBooking_WithConfirmedBooking_ShouldSucceed() {
        // Arrange
        UUID issuerUuid = UUID.randomUUID();
        User issuer = User.builder().uuid(issuerUuid).build();

        when(bookingService.read(any(UUID.class))).thenReturn(sampleBooking);
        when(carService.read(any(UUID.class))).thenReturn(sampleCar);
        when(userService.read(issuerUuid)).thenReturn(issuer);
        when(driverService.read(any(UUID.class))).thenReturn(sampleDriver);
        when(rentalFactory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(sampleRental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(sampleRental);

        // Act
        Rental createdRental = rentalService.createRentalFromBooking(sampleBooking.getUuid(), issuerUuid, sampleDriver.getUuid(), LocalDateTime.now());

        // Assert
        assertNotNull(createdRental);
        verify(carRepository).save(argThat(car -> !car.isAvailable()));
        verify(bookingService).update(argThat(booking -> booking.getStatus() == BookingStatus.RENTAL_INITIATED));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when creating rental from a non-confirmed booking")
    void createRentalFromBooking_WithNonConfirmedBooking_ShouldThrowException() {
        // Arrange
        sampleBooking = new Booking.Builder().setStatus(BookingStatus.PENDING).build();
        when(bookingService.read(any(UUID.class))).thenReturn(sampleBooking);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> rentalService.createRentalFromBooking(sampleBooking.getUuid(), UUID.randomUUID(), null, null));
    }
    @Test
    @DisplayName("Should complete a rental and make the car available")
    void completeRentalByUuid_WithActiveRental_ShouldSucceed() {
        // --- Arrange ---
        // Create an "existing" rental state where the car is unavailable
        Car unavailableCar = new Car.Builder().copy(this.sampleCar).setAvailable(false).build();
        Rental activeRental = new Rental.Builder().copy(this.sampleRental).setCar(unavailableCar).setStatus(RentalStatus.ACTIVE).build();

        // Mock the repository to return this specific state
        when(rentalRepository.findByUuidAndDeletedFalse(activeRental.getUuid())).thenReturn(Optional.of(activeRental));

        // --- Act ---
        rentalService.completeRentalByUuid(activeRental.getUuid(), 0);

        // --- Assert ---
        // Verify car repository was told to save a car that is now AVAILABLE
        verify(carRepository, times(1)).save(argThat(Car::isAvailable));
        // Verify rental repository was told to save a rental that is now COMPLETED
        verify(rentalRepository, times(1)).save(argThat(r -> r.getStatus() == RentalStatus.COMPLETED && r.getReturnedDate() != null));
    }

    @Test
    @DisplayName("Should soft delete a rental and make car available if it was active")
    void delete_WithActiveRental_ShouldMakeCarAvailable() {
        // --- Arrange ---
        // Create an "existing" rental state where the car is unavailable
        Car unavailableCar = new Car.Builder().copy(this.sampleCar).setAvailable(false).build();
        Rental activeRental = new Rental.Builder().copy(this.sampleRental).setCar(unavailableCar).setStatus(RentalStatus.ACTIVE).build();

        // Mock the repository to return this specific state when searched by its ID
        when(rentalRepository.findByIdAndDeletedFalse(activeRental.getId())).thenReturn(Optional.of(activeRental));

        // --- Act ---
        boolean result = rentalService.delete(activeRental.getId());

        // --- Assert ---
        assertTrue(result);
        // Verify car repository was told to save a car that is now AVAILABLE
        verify(carRepository, times(1)).save(argThat(Car::isAvailable));
        // Verify rental was saved as deleted and CANCELLED
        verify(rentalRepository, times(1)).save(argThat(r -> r.isDeleted() && r.getStatus() == RentalStatus.CANCELLED));
    }
}