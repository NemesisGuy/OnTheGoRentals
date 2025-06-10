package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IDriverRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DriverServiceImpl}.
 * Tests CRUD operations and business logic for Driver entities.
 */
@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private IDriverRepository driverRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver sampleDriver;
    private Driver driverToCreate;
    private UUID commonUuid;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        commonUuid = UUID.randomUUID();
        fixedTime = LocalDateTime.now();

        sampleDriver = new Driver.Builder()
                .setId(1)
                .setUuid(commonUuid)
                .setFirstName("John")
                .setLastName("Ryder")
                .setLicenseCode("C1")
                .setDeleted(false)
                .setCreatedAt(fixedTime.minusDays(5))
                .setUpdatedAt(fixedTime.minusDays(1))
                .build();

        driverToCreate = new Driver.Builder()
                .setFirstName("Jane")
                .setLastName("Driver")
                .setLicenseCode("B")
                // UUID, createdAt, updatedAt, deleted will be handled by service/PrePersist
                .build();
    }

    // --- Create Tests ---
    @Test
    void create_shouldSaveAndReturnDriver_withGeneratedFields() {
        Driver builtByService = new Driver.Builder()
                .copy(driverToCreate)
                .setUuid(UUID.randomUUID())
                .setCreatedAt(LocalDateTime.now()) // These would be set by @PrePersist
                .setUpdatedAt(LocalDateTime.now())
                .setDeleted(false)
                .build();
        Driver savedEntity = new Driver.Builder().copy(builtByService).setId(10).build(); // DB generates ID

        when(driverRepository.save(any(Driver.class))).thenReturn(savedEntity);

        Driver created = driverService.create(driverToCreate);

        assertNotNull(created);
        assertEquals(driverToCreate.getFirstName(), created.getFirstName());
        assertNotNull(created.getId());
        assertNotNull(created.getUuid());
        assertNotNull(created.getCreatedAt());
        assertFalse(created.isDeleted());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void create_shouldUseProvidedUuid_ifSet() {
        UUID specificUuid = UUID.randomUUID();
        Driver driverWithUuid = new Driver.Builder()
                .copy(driverToCreate)
                .setUuid(specificUuid)
                .build();

        Driver savedEntity = new Driver.Builder().copy(driverWithUuid).setId(11).build();
        when(driverRepository.save(any(Driver.class))).thenReturn(savedEntity);

        Driver created = driverService.create(driverWithUuid);

        assertNotNull(created);
        assertEquals(specificUuid, created.getUuid());
        verify(driverRepository).save(argThat(d -> d.getUuid().equals(specificUuid)));
    }

    // --- Read by Integer ID Tests ---
    @Test
    void readByIntegerId_shouldReturnDriver_whenFoundAndNotDeleted() {
        when(driverRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleDriver));
        Driver found = driverService.read(1);
        assertNotNull(found);
        assertEquals(sampleDriver.getUuid(), found.getUuid());
        verify(driverRepository).findByIdAndDeletedFalse(1);
    }

    @Test
    void readByIntegerId_shouldReturnNull_whenNotFound() {
        when(driverRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        Driver found = driverService.read(99);
        assertNull(found);
        verify(driverRepository).findByIdAndDeletedFalse(99);
    }

    // --- Read by UUID Tests ---
    @Test
    void readByUuid_shouldReturnDriver_whenFoundAndNotDeleted() {
        when(driverRepository.findByUuidAndDeletedFalse(commonUuid)).thenReturn(Optional.of(sampleDriver));
        Driver found = driverService.read(commonUuid);
        assertNotNull(found);
        assertEquals(sampleDriver.getId(), found.getId());
        verify(driverRepository).findByUuidAndDeletedFalse(commonUuid);
    }

    @Test
    void readByUuid_shouldReturnNull_whenNotFound() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(driverRepository.findByUuidAndDeletedFalse(nonExistentUuid)).thenReturn(Optional.empty());
        Driver found = driverService.read(nonExistentUuid);
        assertNull(found);
        verify(driverRepository).findByUuidAndDeletedFalse(nonExistentUuid);
    }

    // --- Update Tests ---
    @Test
    void update_shouldUpdateAndReturnDriver_whenFoundAndNotDeleted() {
        Driver updatesToApply = new Driver.Builder()
                .copy(sampleDriver) // Has ID 1 and commonUuid
                .setLastName("Driver-Smith")
                .setLicenseCode("EC1")
                .build(); // @PreUpdate will set updatedAt

        Driver updatedAndSavedDriver = new Driver.Builder()
                .copy(updatesToApply)
                .setUpdatedAt(LocalDateTime.now()) // Simulate @PreUpdate
                .build();

        when(driverRepository.existsByIdAndDeletedFalse(sampleDriver.getId())).thenReturn(true);
        when(driverRepository.save(any(Driver.class))).thenReturn(updatedAndSavedDriver);

        Driver updated = driverService.update(updatesToApply);

        assertNotNull(updated);
        assertEquals(updatesToApply.getLastName(), updated.getLastName());
        assertEquals(updatesToApply.getLicenseCode(), updated.getLicenseCode());
        verify(driverRepository).existsByIdAndDeletedFalse(sampleDriver.getId());
        verify(driverRepository).save(updatesToApply);
    }

    @Test
    void update_shouldThrowIllegalArgumentException_whenIdIsNull() {
        Driver driverWithDefaultId = new Driver.Builder().setFirstName("No").setLastName("ID").build(); // ID will be 0
        // As ID is primitive int, it can't be null. The service checks for null Integer.
        // If ID were Integer:
        // Driver driverWithNullId = mock(Driver.class);
        // when(driverWithNullId.getId()).thenReturn(null);

        // Test for current scenario: ID 0 (default int) and not found
        when(driverRepository.existsByIdAndDeletedFalse(0)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.update(driverWithDefaultId);
        });
        assertTrue(exception.getMessage().contains("Driver not found with ID: 0 for update."));
        verify(driverRepository).existsByIdAndDeletedFalse(0);
        verify(driverRepository, never()).save(any(Driver.class));
    }

   /* @Test
    void update_shouldThrowIllegalArgumentException_whenIdIsNull_ifIdWereInteger() {
        Driver driverWithNullIdObject = mock(Driver.class);
        when(driverWithNullIdObject.getId()).thenReturn(null); // Stub getId() to return null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            driverService.update(driverWithNullIdObject);
        });
        assertEquals("Driver ID cannot be null for update.", exception.getMessage());
        verify(driverRepository, never()).existsByIdAndDeletedFalse(any());
        verify(driverRepository, never()).save(any(Driver.class));
    }*/


    @Test
    void update_shouldThrowResourceNotFoundException_whenDriverNotFound() {
        Driver nonExistentDriver = new Driver.Builder()
                .setId(99)
                .setUuid(UUID.randomUUID())
                .setFirstName("Ghost")
                .build();
        when(driverRepository.existsByIdAndDeletedFalse(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            driverService.update(nonExistentDriver);
        });
        assertEquals("Driver not found with ID: 99 for update.", exception.getMessage());
        verify(driverRepository).existsByIdAndDeletedFalse(99);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    // --- Delete by Integer ID Tests ---
    @Test
    void deleteByIntegerId_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(driverRepository.findByIdAndDeletedFalse(sampleDriver.getId())).thenReturn(Optional.of(sampleDriver));
        doAnswer(invocation -> {
            Driver arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted()); return arg;
        }).when(driverRepository).save(any(Driver.class));


        boolean result = driverService.delete(sampleDriver.getId());

        assertTrue(result);
        verify(driverRepository).findByIdAndDeletedFalse(sampleDriver.getId());
        verify(driverRepository).save(argThat(d -> d.isDeleted() && d.getId() == sampleDriver.getId()));
    }

    @Test
    void deleteByIntegerId_shouldReturnFalse_whenNotFound() {
        when(driverRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        boolean result = driverService.delete(99);
        assertFalse(result);
        verify(driverRepository).findByIdAndDeletedFalse(99);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    // --- Delete by UUID Tests ---
    @Test
    void deleteByUuid_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(driverRepository.findByUuidAndDeletedFalse(commonUuid)).thenReturn(Optional.of(sampleDriver));
        doAnswer(invocation -> {
            Driver arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted()); return arg;
        }).when(driverRepository).save(any(Driver.class));

        boolean result = driverService.delete(commonUuid);

        assertTrue(result);
        verify(driverRepository).findByUuidAndDeletedFalse(commonUuid);
        verify(driverRepository).save(argThat(d -> d.isDeleted() && d.getUuid().equals(commonUuid)));
    }

    @Test
    void deleteByUuid_shouldReturnFalse_whenNotFound() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(driverRepository.findByUuidAndDeletedFalse(nonExistentUuid)).thenReturn(Optional.empty());
        boolean result = driverService.delete(nonExistentUuid);
        assertFalse(result);
        verify(driverRepository).findByUuidAndDeletedFalse(nonExistentUuid);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    // --- GetAll Tests ---
    @Test
    void getAll_shouldReturnListOfNonDeletedDrivers() {
        Driver anotherDriver = new Driver.Builder().setId(2).setUuid(UUID.randomUUID()).setFirstName("Another").build();
        List<Driver> list = List.of(sampleDriver, anotherDriver);
        when(driverRepository.findByDeletedFalse()).thenReturn(list);

        List<Driver> result = driverService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(driverRepository).findByDeletedFalse();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoNonDeletedDriversExist() {
        when(driverRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        List<Driver> result = driverService.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(driverRepository).findByDeletedFalse();
    }
}