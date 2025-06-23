package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.CarRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CarServiceImpl}.
 * <p>
 * Author: Peter Buckingham
 * Updated: 2025-05-30
 */
@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car sampleCar1;
    private Car sampleCar2;
    private UUID sampleUuid1;

    @BeforeEach
    void setUp() {
        sampleUuid1 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        sampleCar1 = new Car.Builder()
                .setId(1)
                .setUuid(sampleUuid1)
                .setMake("Toyota")
                .setModel("Corolla")
                .setYear(2022)
                .setCategory("Sedan")
                .setPriceGroup(PriceGroup.ECONOMY)
                .setLicensePlate("CAA123")
                .setAvailable(true)
                .setDeleted(false)
                .setCreatedAt(now.minusDays(1)) // Simulate existing audit fields
                .setUpdatedAt(now.minusHours(1))
                .build();

        sampleCar2 = new Car.Builder() // ... (as before) ...
                .setId(2).setUuid(UUID.randomUUID()).setMake("Honda").setModel("Civic")
                .setYear(2021).setCategory("Sedan").setPriceGroup(PriceGroup.STANDARD)
                .setLicensePlate("CFM456").setAvailable(false).setDeleted(false)
                .setCreatedAt(now.minusDays(2)).setUpdatedAt(now.minusHours(2))
                .build();
    }

    // --- create Tests ---
    @Test
    void create_shouldSaveAndReturnCar() {
        Car carToCreate = new Car.Builder()
                .setMake("Ford").setModel("Focus").setYear(2023)
                .setCategory("Hatchback").setPriceGroup(PriceGroup.STANDARD)
                .setLicensePlate("CBL789").setAvailable(true)
                .build(); // ID, UUID, deleted, audit fields set by @PrePersist or save

        // Mock what repository.save returns
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> {
            Car saved = invocation.getArgument(0);
            // Simulate DB setting ID and @PrePersist setting UUID/timestamps if they weren't on 'saved'
            return new Car.Builder().copy(saved)
                    .setId(3) // Simulate generated ID
                    .setUuid(saved.getUuid() != null ? saved.getUuid() : UUID.randomUUID())
                    .setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt() : LocalDateTime.now())
                    .setUpdatedAt(saved.getUpdatedAt() != null ? saved.getUpdatedAt() : LocalDateTime.now())
                    .setDeleted(false) // Ensure default
                    .build();
        });

        Car createdCar = carService.create(carToCreate);

        assertNotNull(createdCar);
        assertEquals(3, createdCar.getId());
        assertNotNull(createdCar.getUuid());
        assertEquals("Ford", createdCar.getMake());
        verify(carRepository).save(any(Car.class));
    }


    // --- update Tests ---
    @Test
    void update_shouldUpdateAndReturnCar_whenCarExists() {
        // sampleCar1 is the existing state in DB
        // It has createdAt and updatedAt from setUp()

        // This represents the incoming update data (e.g., from a DTO)
        // It only contains the fields that are being changed + ID/UUID for identification.
        Car carWithUpdatesFromDto = new Car.Builder()
                .setId(sampleCar1.getId())
                .setUuid(sampleCar1.getUuid())
                .setMake("Toyota") // Assuming make might not change, or comes from DTO
                .setModel("Corolla X") // Changed model
                .setYear(sampleCar1.getYear()) // Assuming year not changed in this DTO
                .setCategory(sampleCar1.getCategory())
                .setPriceGroup(PriceGroup.SPECIAL) // Changed price group
                .setLicensePlate(sampleCar1.getLicensePlate())
                .setAvailable(false) // Changed availability
                // Crucially, DO NOT set createdAt or updatedAt here, as they are managed
                .setDeleted(false) // Preserve deleted status from existing
                .build();

        // 1. Mock fetching the existing car by ID (as per service logic)
        when(carRepository.findByIdAndDeletedFalse(sampleCar1.getId())).thenReturn(Optional.of(sampleCar1));

        // 2. Mock the save operation.
        // The object passed to save by the service will be 'entityToSave'.
        // The mock should return an object that reflects what the DB + @PreUpdate would do.
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> {
            Car carBeingSaved = invocation.getArgument(0); // This is 'entityToSave' from the service
            // Simulate that @PreUpdate sets a new updatedAt
            return new Car.Builder().copy(carBeingSaved)
                    .setUpdatedAt(LocalDateTime.now()) // Simulate @PreUpdate
                    .build();
        });

        // Act: Call the service method with the DTO-like update object
        Car resultFromService = carService.update(carWithUpdatesFromDto);

        // Assert
        assertNotNull(resultFromService);
        assertEquals(sampleCar1.getId(), resultFromService.getId());
        assertEquals(sampleCar1.getUuid(), resultFromService.getUuid());
        assertEquals("Toyota", resultFromService.getMake());
        assertEquals("Corolla X", resultFromService.getModel());
        assertEquals(PriceGroup.SPECIAL, resultFromService.getPriceGroup());
        assertFalse(resultFromService.isAvailable());
        assertEquals(sampleCar1.getCreatedAt(), resultFromService.getCreatedAt(), "CreatedAt should be preserved"); // Preserved from existing
        assertNotNull(resultFromService.getUpdatedAt(), "UpdatedAt should not be null");
        assertTrue(resultFromService.getUpdatedAt().isAfter(sampleCar1.getUpdatedAt()), "UpdatedAt should be newer");

        // Verify that the object passed to carRepository.save() inside the service
        // was correctly constructed by the service's builder logic
        verify(carRepository).save(argThat(savedCar ->
                savedCar.getId() == sampleCar1.getId() &&
                        savedCar.getUuid().equals(sampleCar1.getUuid()) &&
                        "Toyota".equals(savedCar.getMake()) &&       // From carWithUpdatesFromDto
                        "Corolla X".equals(savedCar.getModel()) &&   // From carWithUpdatesFromDto
                        !savedCar.isAvailable() &&                   // From carWithUpdatesFromDto
                        savedCar.getPriceGroup() == PriceGroup.SPECIAL && // From carWithUpdatesFromDto
                        savedCar.getCreatedAt().equals(sampleCar1.getCreatedAt()) // Preserved from existing
        ));
    }


    @Test
    void update_shouldThrowResourceNotFound_whenCarNotFoundByIdForUpdate() {
        Car nonExistentCarUpdate = new Car.Builder().setId(99).setMake("Ghost").build();
        when(carRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            carService.update(nonExistentCarUpdate);
        });
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void update_shouldThrowIllegalArgument_whenCarIdIsNull() {
        Car carWithNullId = new Car.Builder().setMake("NoID").setUuid(UUID.randomUUID()).build(); // ID is null (or 0 if int)
        // carWithNullId.setId(0); // If ID is primitive int

        assertThrows(IllegalArgumentException.class, () -> {
            carService.update(carWithNullId);
        });
        verify(carRepository, never()).findByIdAndDeletedFalse(any());
        verify(carRepository, never()).findByUuidAndDeletedFalse(any());
        verify(carRepository, never()).save(any(Car.class));
    }


    // --- delete by ID Tests ---
    @Test
    void deleteById_shouldSoftDeleteAndMarkUnavailable_whenCarExists() {
        // Ensure sampleCar1 is available before deletion for this test path
        sampleCar1 = new Car.Builder().copy(sampleCar1).setAvailable(true).build();

        when(carRepository.findByIdAndDeletedFalse(sampleCar1.getId())).thenReturn(Optional.of(sampleCar1));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = carService.delete(sampleCar1.getId());

        assertTrue(result);
        verify(carRepository).save(argThat(car ->
                car.isDeleted() && !car.isAvailable() && car.getId() == sampleCar1.getId()
        ));
    }

    // ... (other tests for read, getAll, specific finders remain largely the same, ensure .build() is used)
    // ... (and ensure mocks return Optional where repository methods return Optional)
    @Test
    void readById_shouldReturnCar_whenFoundAndNotDeleted() {
        when(carRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleCar1));
        Car foundCar = carService.read(1);
        assertNotNull(foundCar);
        assertEquals(sampleCar1.getMake(), foundCar.getMake());
    }

  /*  @Test
    void readById_shouldReturnNull_whenNotFoundOrDeleted() {
        when(carRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        Car foundCar = carService.read(99);
        assertNull(foundCar);
    }*/

    @Test
    void readByUuid_shouldReturnCar_whenFoundAndNotDeleted() {
        when(carRepository.findByUuidAndDeletedFalse(sampleUuid1)).thenReturn(Optional.of(sampleCar1));
        Car foundCar = carService.read(sampleUuid1);
        assertNotNull(foundCar);
        assertEquals(sampleCar1.getMake(), foundCar.getMake());
    }

    @Test
    void deleteById_shouldReturnFalse_whenCarNotFound() {
        when(carRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        boolean result = carService.delete(99);
        assertFalse(result);
        verify(carRepository, never()).save(any(Car.class));
    }

 /*   @Test
    void deleteByUuid_shouldSoftDeleteAndMarkUnavailable_whenCarExists() {
        when(carRepository.findByUuidAndDeletedFalse(sampleUuid1)).thenReturn(Optional.of(sampleCar1));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = carService.delete(sampleUuid1);

        assertTrue(result);
        verify(carRepository).save(argThat(car ->
                car.isDeleted() && !car.isAvailable() && car.getUuid().equals(sampleUuid1)
        ));
    }*/

    @Test
    void getAll_shouldReturnListOfNonDeletedCars() {
        when(carRepository.findByDeletedFalse()).thenReturn(List.of(sampleCar1, sampleCar2));
        List<Car> cars = carService.getAll();
        assertEquals(2, cars.size());
    }

    @Test
    void getAllAvailableCars_shouldReturnOnlyAvailableAndNonDeleted() {
        when(carRepository.findAllByAvailableTrueAndDeletedFalse()).thenReturn(List.of(sampleCar1));
        List<Car> availableCars = carService.getAllAvailableCars();
        assertEquals(1, availableCars.size());
        assertTrue(availableCars.get(0).isAvailable());
    }

    // ... (other finder tests like getCarsByPriceGroup etc.)
}