package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.DamageReport;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IDamageReportRepository;
import za.ac.cput.service.IRentalService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DamageReportServiceImpl}.
 * Tests CRUD operations and business logic for DamageReport entities.
 */
@ExtendWith(MockitoExtension.class)
class DamageReportServiceImplTest {

    @Mock
    private IDamageReportRepository damageReportRepository;

    @Mock
    private IRentalService rentalService;

    @InjectMocks
    private DamageReportServiceImpl damageReportService;

    private DamageReport sampleDamageReport;
    private Rental sampleRental;
    private UUID commonReportUuid;
    private UUID commonRentalUuid;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        commonRentalUuid = UUID.randomUUID();
        commonReportUuid = UUID.randomUUID();
        fixedTime = LocalDateTime.now();

        sampleRental = Rental.builder()
                .setUuid(commonRentalUuid)
                .setId(1)
                .build();

        sampleDamageReport = new DamageReport.Builder()
                .setId(1)
                .setUuid(commonReportUuid)
                .setRental(sampleRental)
                .setDescription("Scratch on the left door.")
                .setDateAndTime(fixedTime.minusDays(1))
                .setLocation("Parking Lot A")
                .setRepairCost(150.00)
                .setCreatedAt(fixedTime.minusDays(1))
                .setUpdatedAt(fixedTime.minusHours(2))
                .setDeleted(false)
                .build();
    }

    // --- Create Tests ---
    @Test
    void create_shouldSaveAndReturnReport_whenRentalExists() {
        DamageReport reportToCreate = new DamageReport.Builder()
                .setRental(sampleRental)
                .setDescription("New dent on bumper.")
                .setLocation("Highway Exit 5")
                .build();

        DamageReport savedReport = new DamageReport.Builder()
                .copy(reportToCreate)
                .setUuid(UUID.randomUUID())
                .setDateAndTime(LocalDateTime.now())
                .setCreatedAt(LocalDateTime.now())
                .setDeleted(false)
                .setId(2)
                .build();
        savedReport = new DamageReport.Builder().copy(savedReport).setUpdatedAt(savedReport.getCreatedAt()).build();


        when(rentalService.read(sampleRental.getUuid())).thenReturn(sampleRental);
        when(damageReportRepository.save(any(DamageReport.class))).thenReturn(savedReport);

        DamageReport created = damageReportService.create(reportToCreate);

        assertNotNull(created);
        assertEquals(savedReport.getDescription(), created.getDescription());
        assertNotNull(created.getId());
        assertNotNull(created.getUuid());
        assertNotNull(created.getDateAndTime());
        assertNotNull(created.getCreatedAt());
        assertFalse(created.isDeleted());
        assertEquals(sampleRental, created.getRental());
        verify(rentalService).read(sampleRental.getUuid());
        verify(damageReportRepository).save(any(DamageReport.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenRentalIsNull() {
        DamageReport reportWithNullRental = new DamageReport.Builder()
                .setRental(null)
                .setDescription("Test")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.create(reportWithNullRental);
        });
        assertEquals("Rental and Rental UUID must be provided for a damage report.", exception.getMessage());
        verify(rentalService, never()).read(any(UUID.class));
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenRentalUuidIsNull() {
        // Create a Rental mock that returns null for getUuid()
        Rental rentalWithNullUuidSpy = spy(Rental.builder().setId(1).build()); // Spy on a real object or use a mock
        doReturn(null).when(rentalWithNullUuidSpy).getUuid();


        DamageReport reportWithNullRentalUuid = new DamageReport.Builder()
                .setRental(rentalWithNullUuidSpy) // Use the spy/mock
                .setDescription("Test where rental object exists but its UUID is null")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.create(reportWithNullRentalUuid);
        });
        assertEquals("Rental and Rental UUID must be provided for a damage report.", exception.getMessage());
        verify(rentalService, never()).read(any(UUID.class)); // Should not proceed to read if UUID is null
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }


    @Test
    void create_shouldThrowResourceNotFoundException_whenRentalDoesNotExist() {
        UUID nonExistentRentalUuid = UUID.randomUUID();
        Rental rentalForReport = Rental.builder().setUuid(nonExistentRentalUuid).setId(99).build();
        DamageReport reportToCreate = new DamageReport.Builder()
                .setRental(rentalForReport)
                .setDescription("Test")
                .build();

        when(rentalService.read(nonExistentRentalUuid)).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            damageReportService.create(reportToCreate);
        });
        assertEquals("Associated Rental not found with UUID: " + nonExistentRentalUuid, exception.getMessage());
        verify(rentalService).read(nonExistentRentalUuid);
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }

    // --- Read by UUID Tests ---
    @Test
    void readByUuid_shouldReturnReport_whenFoundAndNotDeleted() {
        when(damageReportRepository.findByUuidAndDeletedFalse(commonReportUuid)).thenReturn(Optional.of(sampleDamageReport));
        DamageReport found = damageReportService.read(commonReportUuid);
        assertNotNull(found);
        assertEquals(sampleDamageReport.getId(), found.getId());
        verify(damageReportRepository).findByUuidAndDeletedFalse(commonReportUuid);
    }

    @Test
    void readByUuid_shouldReturnNull_whenNotFound() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(damageReportRepository.findByUuidAndDeletedFalse(nonExistentUuid)).thenReturn(Optional.empty());
        DamageReport found = damageReportService.read(nonExistentUuid);
        assertNull(found);
        verify(damageReportRepository).findByUuidAndDeletedFalse(nonExistentUuid);
    }

    // --- Read by Integer ID Tests (Interface method) ---
    @Test
    void readByIntegerId_shouldReturnReport_whenFoundAndNotDeleted() {
        when(damageReportRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleDamageReport));
        DamageReport found = damageReportService.read(Integer.valueOf(1)); // Use Integer object
        assertNotNull(found);
        assertEquals(sampleDamageReport.getUuid(), found.getUuid());
        verify(damageReportRepository).findByIdAndDeletedFalse(1);
    }

    @Test
    void readByIntegerId_shouldReturnNull_whenNotFound() {
        when(damageReportRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        DamageReport found = damageReportService.read(Integer.valueOf(99)); // Use Integer object
        assertNull(found);
        verify(damageReportRepository).findByIdAndDeletedFalse(99);
    }

    // --- Read by int ID (Deprecated) Tests ---
    @Test
    @SuppressWarnings("deprecation")
    void readByIntIdDeprecated_shouldReturnOptionalReport_whenFound() {
        when(damageReportRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleDamageReport));
        Optional<DamageReport> foundOpt = damageReportService.read(1); // Calls deprecated read(int id)
        assertTrue(foundOpt.isPresent());
        assertEquals(sampleDamageReport.getUuid(), foundOpt.get().getUuid());
        verify(damageReportRepository).findByIdAndDeletedFalse(1);
    }

    @Test
    @SuppressWarnings("deprecation")
    void readByIntIdDeprecated_shouldReturnEmptyOptional_whenNotFound() {
        when(damageReportRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        Optional<DamageReport> foundOpt = damageReportService.read(99); // Calls deprecated read(int id)
        assertFalse(foundOpt.isPresent());
        verify(damageReportRepository).findByIdAndDeletedFalse(99);
    }


    // --- Update Tests ---
    @Test
    void update_shouldUpdateAndReturnReport_whenValid() {
        DamageReport updatesToApply = new DamageReport.Builder()
                .copy(sampleDamageReport)
                .setDescription("Updated description: Severe dent and paint chip.")
                .setRepairCost(250.00)
                .build();

        DamageReport updatedAndSavedReport = new DamageReport.Builder()
                .copy(updatesToApply)
                .setUpdatedAt(LocalDateTime.now())
                .build();

        when(damageReportRepository.findByIdAndDeletedFalse(sampleDamageReport.getId())).thenReturn(Optional.of(sampleDamageReport));
        when(damageReportRepository.save(any(DamageReport.class))).thenReturn(updatedAndSavedReport);

        DamageReport updated = damageReportService.update(updatesToApply);

        assertNotNull(updated);
        assertEquals(updatesToApply.getDescription(), updated.getDescription());
        assertEquals(updatesToApply.getRepairCost(), updated.getRepairCost());
        assertEquals(sampleDamageReport.getRental().getUuid(), updated.getRental().getUuid());
        verify(damageReportRepository).findByIdAndDeletedFalse(sampleDamageReport.getId());
        verify(damageReportRepository).save(updatesToApply);
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenIdIsZeroAndNotExists() {
        DamageReport reportWithDefaultId = new DamageReport.Builder()
                .setRental(sampleRental).setDescription("ID is 0").build(); // ID will be 0

        when(damageReportRepository.findByIdAndDeletedFalse(0)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            damageReportService.update(reportWithDefaultId);
        });
        assertTrue(exception.getMessage().contains("DamageReport not found with ID: 0 for update."));
        verify(damageReportRepository).findByIdAndDeletedFalse(0);
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }

/*
    // IF DamageReport.id were Integer
    @Test
    void update_shouldThrowIllegalArgumentException_whenIdObjectIsNull() {
        DamageReport reportWithNullId = new DamageReport.Builder()
                // Explicitly set the Integer ID to null, or don't set it if builder defaults to null
                .setId(0) // Assuming builder.setId(Integer id)
                .setRental(sampleRental)
                .setDescription("ID is a null Integer")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.update(reportWithNullId);
        });
        assertEquals("DamageReport ID cannot be null for update.", exception.getMessage());
        verify(damageReportRepository, never()).findByIdAndDeletedFalse(any());
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }
*/


    @Test
    void update_shouldThrowResourceNotFoundException_whenReportNotFound() {
        DamageReport nonExistentReport = new DamageReport.Builder()
                .setId(99)
                .setUuid(UUID.randomUUID())
                .setRental(sampleRental)
                .setDescription("Non Existent")
                .build();
        when(damageReportRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            damageReportService.update(nonExistentReport);
        });
        assertEquals("DamageReport not found with ID: 99 for update.", exception.getMessage());
        verify(damageReportRepository).findByIdAndDeletedFalse(99);
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }

    @Test
    void update_shouldThrowIllegalArgumentException_whenRentalIsChanged() {
        Rental differentRental = Rental.builder().setUuid(UUID.randomUUID()).setId(2).build();
        DamageReport updatesWithDifferentRental = new DamageReport.Builder()
                .copy(sampleDamageReport)
                .setRental(differentRental)
                .build();

        when(damageReportRepository.findByIdAndDeletedFalse(sampleDamageReport.getId())).thenReturn(Optional.of(sampleDamageReport));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.update(updatesWithDifferentRental);
        });
        assertEquals("The associated Rental of a DamageReport cannot be changed post-creation.", exception.getMessage());
        verify(damageReportRepository).findByIdAndDeletedFalse(sampleDamageReport.getId());
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }

    @Test
    void update_shouldThrowIllegalArgumentException_whenUpdatedRentalIsNull() {
        DamageReport updatesWithNullRental = new DamageReport.Builder()
                .copy(sampleDamageReport)
                .setRental(null)
                .build();

        when(damageReportRepository.findByIdAndDeletedFalse(sampleDamageReport.getId())).thenReturn(Optional.of(sampleDamageReport));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            damageReportService.update(updatesWithNullRental);
        });
        assertEquals("The associated Rental of a DamageReport cannot be changed post-creation.", exception.getMessage());
        verify(damageReportRepository).findByIdAndDeletedFalse(sampleDamageReport.getId());
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }


    // --- Delete by Integer ID Tests (delete and deleteById) ---
    @Test
    void deleteByIntegerId_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(damageReportRepository.findByIdAndDeletedFalse(sampleDamageReport.getId())).thenReturn(Optional.of(sampleDamageReport));
        doAnswer(invocation -> {
            DamageReport arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted()); return arg;
        }).when(damageReportRepository).save(any(DamageReport.class));

        boolean result = damageReportService.delete(Integer.valueOf(sampleDamageReport.getId()));

        assertTrue(result);
        verify(damageReportRepository).findByIdAndDeletedFalse(sampleDamageReport.getId());
        verify(damageReportRepository).save(argThat(dr -> dr.isDeleted() && dr.getId() == sampleDamageReport.getId()));
    }

    @Test
    void deleteByIntegerId_shouldReturnFalse_whenNotFound() {
        when(damageReportRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        boolean result = damageReportService.delete(Integer.valueOf(99));
        assertFalse(result);
        verify(damageReportRepository).findByIdAndDeletedFalse(99);
        verify(damageReportRepository, never()).save(any(DamageReport.class));
    }

    @Test
    void deleteByIdPrimitive_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(damageReportRepository.findByIdAndDeletedFalse(sampleDamageReport.getId())).thenReturn(Optional.of(sampleDamageReport));
        doAnswer(invocation -> {
            DamageReport arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted()); return arg;
        }).when(damageReportRepository).save(any(DamageReport.class));

        boolean result = damageReportService.deleteById(sampleDamageReport.getId());

        assertTrue(result);
        verify(damageReportRepository).findByIdAndDeletedFalse(sampleDamageReport.getId());
        verify(damageReportRepository).save(argThat(dr -> dr.isDeleted() && dr.getId() == sampleDamageReport.getId()));
    }


    // --- GetAll Tests ---
    @Test
    void getAll_shouldReturnListOfNonDeletedReports() {
        DamageReport anotherReport = new DamageReport.Builder().setId(2).setUuid(UUID.randomUUID()).setRental(sampleRental).setDescription("Another report").build();
        List<DamageReport> list = List.of(sampleDamageReport, anotherReport);
        when(damageReportRepository.findByDeletedFalse()).thenReturn(list);

        List<DamageReport> result = damageReportService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(damageReportRepository).findByDeletedFalse();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoNonDeletedEntriesExist() {
        when(damageReportRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        List<DamageReport> result = damageReportService.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(damageReportRepository).findByDeletedFalse();
    }
}