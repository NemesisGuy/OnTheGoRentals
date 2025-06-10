package za.ac.cput.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IDriverRepository;
import za.ac.cput.service.IDriverService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DriverServiceImpl.java
 * Implementation of the {@link IDriverService} interface.
 * Manages Driver entities, including CRUD operations.
 * Entities are treated as immutable; updates are performed using a Builder pattern with a copy method.
 * <p>
 * Author: Peter Buckingham // Assuming based on consistent authorship
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service
public class DriverServiceImpl implements IDriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverServiceImpl.class);
    private final IDriverRepository driverRepository; // Renamed from repository

    /**
     * Constructs the DriverServiceImpl.
     * Note: The original constructor was private. Changed to public for standard Spring component scanning.
     *
     * @param driverRepository The repository for driver persistence.
     */
    @Autowired
    public DriverServiceImpl(IDriverRepository driverRepository) { // Changed constructor to public
        this.driverRepository = driverRepository;
        log.info("DriverServiceImpl initialized.");
    }

    /**
     * {@inheritDoc}
     * Creates a new driver.
     * Ensures the entity is not marked as deleted and assigns a UUID if not present.
     */
    @Override
    @Transactional // Spring's Transactional
    public Driver create(Driver driver) {
        log.info("Attempting to create new driver. Name: {} {}", driver.getFirstName(), driver.getLastName());

        Driver entityToSave;
        if (driver.getUuid() == null) {
            entityToSave = new Driver.Builder().copy(driver)
                    .setUuid(UUID.randomUUID())
                    .setDeleted(false)
                    .build();
            log.debug("Generated UUID '{}' for new driver.", entityToSave.getUuid());
        } else {
            entityToSave = new Driver.Builder().copy(driver)
                    .setDeleted(false) // Ensure not deleted on creation
                    .build();
        }
        // Ensure other defaults like license expiry, availability might be set here or by @PrePersist

        Driver savedDriver = driverRepository.save(entityToSave);
        log.info("Successfully created driver. ID: {}, UUID: '{}', Name: {} {}",
                savedDriver.getId(), savedDriver.getUuid(), savedDriver.getFirstName(), savedDriver.getLastName());
        return savedDriver;
    }

    /**
     * {@inheritDoc}
     * Reads a driver by their internal integer ID.
     */
    @Override
    public Driver read(Integer id) {
        log.debug("Attempting to read driver by ID: {}", id);
        Driver driver = driverRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (driver == null) {
            log.warn("Driver not found or is deleted for ID: {}", id);
        } else {
            log.debug("Driver found for ID: {}. Name: {} {}", id, driver.getFirstName(), driver.getLastName());
        }
        return driver;
    }

    /**
     * {@inheritDoc}
     * Reads a driver by their UUID.
     */
    @Override
    public Driver read(UUID uuid) {
        log.debug("Attempting to read driver by UUID: '{}'", uuid);
        Driver driver = driverRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (driver == null) {
            log.warn("Driver not found or is deleted for UUID: '{}'", uuid);
        } else {
            log.debug("Driver found for UUID: '{}'. ID: {}", uuid, driver.getId());
        }
        return driver;
    }

    /**
     * {@inheritDoc}
     * Updates an existing driver.
     * The input {@code driverWithUpdates} should be the complete new state.
     *
     * @throws ResourceNotFoundException if the driver with the given ID does not exist or is deleted.
     */
    @Override
    @Transactional // Spring's Transactional
    public Driver update(Driver driverWithUpdates) { // Parameter name implies it's the desired new state
        Integer driverId = driverWithUpdates.getId();
        log.info("Attempting to update driver with ID: {}", driverId);

        if (driverId == null) {
            log.error("Update failed: Driver ID cannot be null.");
            throw new IllegalArgumentException("Driver ID cannot be null for update.");
        }
        if (!driverRepository.existsByIdAndDeletedFalse(driverId)) {
            log.warn("Update failed: Driver not found or is deleted for ID: {}", driverId);
            throw new ResourceNotFoundException("Driver not found with ID: " + driverId + " for update.");
        }

        log.debug("Updating driver ID: {}. New Name: {} {}",
                driverId, driverWithUpdates.getFirstName(), driverWithUpdates.getLastName());
        // 'driverWithUpdates' IS the new state, built by the controller.

        Driver savedDriver = driverRepository.save(driverWithUpdates);
        log.info("Successfully updated driver. ID: {}, UUID: '{}'",
                savedDriver.getId(), savedDriver.getUuid());
        return savedDriver;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a driver by their internal integer ID.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(Integer id) {
        log.info("Attempting to soft-delete driver with ID: {}", id);
        Optional<Driver> driverOpt = driverRepository.findByIdAndDeletedFalse(id);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            Driver deletedDriver = new Driver.Builder().copy(driver).setDeleted(true).build();
            driverRepository.save(deletedDriver);
            log.info("Successfully soft-deleted driver with ID: {}", id);
            return true;
        }
        log.warn("Soft-delete failed: Driver not found or already deleted for ID: {}", id);
        return false;
    }

    /**
     * {@inheritDoc}
     * Soft-deletes a driver by their UUID.
     * This method first reads the driver by UUID to get the entity, then builds the
     * deleted state and saves it.
     */
    @Override
    @Transactional // Spring's Transactional
    public boolean delete(UUID uuid) {
        log.info("Attempting to soft-delete driver with UUID: '{}'", uuid);
        Optional<Driver> driverOpt = driverRepository.findByUuidAndDeletedFalse(uuid);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            Driver deletedDriver = new Driver.Builder().copy(driver).setDeleted(true).build();
            driverRepository.save(deletedDriver);
            log.info("Successfully soft-deleted driver with UUID: '{}' (ID: {}).", uuid, driver.getId());
            return true;
        }
        log.warn("Soft-delete by UUID failed: Driver not found or already deleted for UUID: '{}'", uuid);
        return false;
    }

    /**
     * {@inheritDoc}
     * Retrieves all non-deleted drivers.
     */
    @Override
    public List<Driver> getAll() {
        log.debug("Fetching all non-deleted drivers.");
        List<Driver> drivers = driverRepository.findByDeletedFalse();
        log.debug("Found {} non-deleted drivers.", drivers.size());
        return drivers;
    }
}