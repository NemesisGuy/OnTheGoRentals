package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IDamageReportRepository;
import za.ac.cput.service.IRentalService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DamageReportServiceImpl class.
 * Uses Mockito to isolate the service logic and verify its interactions with dependencies.
 */
@ExtendWith(MockitoExtension.class)
class DamageReportServiceImplTest {

    @Mock
    private IDamageReportRepository damageReportRepository;
    @Mock
    private IRentalService rentalService;

    @InjectMocks
    private DamageReportServiceImpl damageReportService;

    private Rental rental;
    private DamageReport damageReport;

    // Set up common test objects before each test runs
    @BeforeEach
    void setUp() {
        UUID rentalUuid = UUID.randomUUID();
        UUID carUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        User user = User.builder().uuid(userUuid).email("test@example.com").build();
        Car car = new Car.Builder().setUuid(carUuid).setMake("Toyota").build();
        rental = new Rental.Builder().setUuid(rentalUuid).setUser(user).setCar(car) .build();

        damageReport = new DamageReport.Builder()
                .setDescription("Scratch on the left door")
                .setRental(rental)
                .build();
    }

    @Test
    @DisplayName("Should create a damage report when a valid rental exists")
    void create_WithValidRental_ShouldSucceed() {
        // --- Arrange ---
        // Mock the rentalService to confirm that the rental exists
        when(rentalService.read(rental.getUuid())).thenReturn(rental);
        // Mock the repository's save method to return the object it was given
        when(damageReportRepository.save(any(DamageReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        DamageReport createdReport = damageReportService.create(damageReport);

        // --- Assert ---
        assertNotNull(createdReport);
        assertNotNull(createdReport.getUuid()); // Should be set by the service if null
        assertNotNull(createdReport.getDateAndTime()); // Should be set if null
        assertFalse(createdReport.isDeleted());
        assertEquals("Scratch on the left door", createdReport.getDescription());

        // Verify that the dependencies were called
        verify(rentalService, times(1)).read(rental.getUuid());
        verify(damageReportRepository, times(1)).save(any(DamageReport.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating a report for a non-existent rental")
    void create_WithNonExistentRental_ShouldThrowException() {
        // --- Arrange ---
        // Mock the rentalService to return null, simulating a non-existent rental
        when(rentalService.read(rental.getUuid())).thenReturn(null);

        // --- Act & Assert ---
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            damageReportService.create(damageReport);
        });

        assertEquals("Associated Rental not found with UUID: " + rental.getUuid(), exception.getMessage());

        // Verify that the save method was never called
        verify(damageReportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating a report without a rental")
    void create_WithNullRental_ShouldThrowException() {
        // --- Arrange ---
        damageReport = new DamageReport.Builder().setRental(null).build(); // Make the rental object null

        // --- Act & Assert ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.create(damageReport);
        });

        assertEquals("Rental and Rental UUID must be provided for a damage report.", exception.getMessage());
        verify(damageReportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully update a damage report")
    void update_WithValidData_ShouldSucceed() {
        // --- Arrange ---
        UUID reportUuid = UUID.randomUUID();
        damageReport = new DamageReport.Builder().copy(damageReport)
                        .setId(1)
                        .setUuid(reportUuid)
                        .setDescription("Deep scratch and dent").build();


        DamageReport updatedState = new DamageReport.Builder()
                .setId(1)
                .setUuid(reportUuid)
                .setDescription("Updated: Deep scratch and dent")
                .setRental(rental) // The rental must remain the same
                .build();

        // Mock finding the existing report
        when(damageReportRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(damageReport));
        // Mock the save operation
        when(damageReportRepository.save(any(DamageReport.class))).thenReturn(updatedState);

        // --- Act ---
        DamageReport result = damageReportService.update(updatedState);

        // --- Assert ---
        assertNotNull(result);
        assertEquals("Updated: Deep scratch and dent", result.getDescription());
        verify(damageReportRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(damageReportRepository, times(1)).save(updatedState);
    }

    // ... inside DamageReportServiceImplTest.java ...

    @Test
    @DisplayName("Should throw IllegalArgumentException when trying to update the rental on a report")
    void update_WhenChangingRental_ShouldThrowException() {
        // --- Arrange ---
        // THE FIX IS HERE: We must provide a valid 'existingReport' that HAS a rental,
        // because we want to test the logic that PREVENTS that rental from being changed.

        // 1. Create a valid "existing" report that we'll pretend is in the database.
        // It has our original `rental` object associated with it.
        DamageReport existingReport = new DamageReport.Builder()
                .setId(1)
                .setRental(this.rental) // Use the rental created in setUp()
                .build();

        // 2. Create a different rental object for the update attempt.
        Rental newRental = new Rental.Builder().setUuid(UUID.randomUUID()).build();

        // 3. Create the incoming DTO/payload that attempts the illegal change.
        DamageReport updatedStateWithDifferentRental = new DamageReport.Builder()
                .setId(1)
                .setRental(newRental) // Attempting to change the rental
                .build();

        // 4. Mock the repository to return our valid "existing" report.
        when(damageReportRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(existingReport));

        // --- Act & Assert ---
        // Now, the test will correctly check the part of the code that compares the two different rental UUIDs.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.update(updatedStateWithDifferentRental);
        });

        // Assert the correct error message is thrown.
        assertEquals("The associated Rental of a DamageReport cannot be changed post-creation.", exception.getMessage());

        // Verify that the save method was never called because the validation failed.
        verify(damageReportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should soft-delete a damage report by ID")
    void deleteById_WhenReportExists_ShouldReturnTrue() {
        // --- Arrange ---
        damageReport=  new DamageReport.Builder().setId(1).build();
        when(damageReportRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(damageReport));

        // --- Act ---
        boolean result = damageReportService.deleteById(1);

        // --- Assert ---
        assertTrue(result);
        // Verify that the save method was called on an entity that is marked as deleted
        verify(damageReportRepository, times(1)).save(argThat(report -> report.isDeleted()));
    }

    @Test
    @DisplayName("Should return false when trying to delete a non-existent report")
    void deleteById_WhenReportDoesNotExist_ShouldReturnFalse() {
        // --- Arrange ---
        when(damageReportRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());

        // --- Act ---
        boolean result = damageReportService.deleteById(99);

        // --- Assert ---
        assertFalse(result);
        verify(damageReportRepository, never()).save(any());
    }
}