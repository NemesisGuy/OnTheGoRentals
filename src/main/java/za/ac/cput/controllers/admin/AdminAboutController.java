package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.domain.dto.request.AboutUsCreateDTO;
import za.ac.cput.domain.dto.request.AboutUsUpdateDTO;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.mapper.AboutUsMapper;
import za.ac.cput.exception.ResourceNotFoundException; // Assuming this exists for consistency
import za.ac.cput.service.IAboutUsService;

import java.util.List;
import java.util.UUID;

/**
 * AdminAboutController.java
 * Admin Controller for managing "About Us" page content.
 * Allows administrators to create, retrieve, update, and delete "About Us" entries.
 * External identification of "About Us" entries is done via UUIDs, while internal
 * service operations primarily use integer IDs. This controller bridges that gap.
 **
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 **
 * Updated by: Peter Buckingham / System
 **
 * Updated: [2025-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/about-us")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')") // Apply security if all endpoints are admin-only
public class AdminAboutController {

    private static final Logger log = LoggerFactory.getLogger(AdminAboutController.class);
    private final IAboutUsService aboutUsService;

    /**
     * Constructs an AdminAboutController with the necessary AboutUs service.
     *
     * @param aboutUsService The service implementation for "About Us" operations.
     */
    @Autowired
    public AdminAboutController(IAboutUsService aboutUsService) {
        this.aboutUsService = aboutUsService;
        log.info("AdminAboutController initialized.");
    }

    /**
     * Creates a new "About Us" information entry.
     * While typically there might be a single "About Us" page, this endpoint
     * allows for the creation of new entries, which could be useful for versioning
     * or if multiple distinct "About Us" sections are supported.
     *
     * @param createDto The {@link AboutUsCreateDTO} containing the data for the new entry.
     * @return A ResponseEntity containing the created {@link AboutUsResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<AboutUsResponseDTO> createAboutUs(@Valid @RequestBody AboutUsCreateDTO createDto) {
        log.info("Admin request to create new About Us content with DTO: {}", createDto);
        AboutUs aboutUsToCreate = AboutUsMapper.toEntity(createDto);
        log.debug("Mapped DTO to AboutUs entity for creation: {}", aboutUsToCreate);

        AboutUs createdEntity = aboutUsService.create(aboutUsToCreate);
        log.info("Successfully created About Us entry with ID: {} and UUID: {}", createdEntity.getId(), createdEntity.getUuid()); // Assuming getAboutUsId() is the UUID field
        return new ResponseEntity<>(AboutUsMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific "About Us" entry by its UUID.
     *
     * @param aboutUsUuid The UUID of the "About Us" entry to retrieve.
     * @return A ResponseEntity containing the {@link AboutUsResponseDTO} if found.
     * @throws ResourceNotFoundException if the "About Us" entry with the given UUID is not found (handled by service).
     */
    @GetMapping("/{aboutUsUuid}")
    public ResponseEntity<AboutUsResponseDTO> getAboutUsByUuid(@PathVariable UUID aboutUsUuid) {
        log.info("Admin request to get About Us content by UUID: {}", aboutUsUuid);
        AboutUs aboutUsEntity = aboutUsService.read(aboutUsUuid);
        // The aboutUsService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved About Us entry with ID: {} for UUID: {}", aboutUsEntity.getId(), aboutUsUuid);
        return ResponseEntity.ok(AboutUsMapper.toDto(aboutUsEntity));
    }

    /**
     * Updates an existing "About Us" entry identified by its UUID.
     *
     * @param aboutUsUuid The UUID of the "About Us" entry to update.
     * @param updateDto   The {@link AboutUsUpdateDTO} containing the updated data.
     * @return A ResponseEntity containing the updated {@link AboutUsResponseDTO}.
     * @throws ResourceNotFoundException if the "About Us" entry with the given UUID is not found (handled by service).
     */
    @PutMapping("/{aboutUsUuid}")
    public ResponseEntity<AboutUsResponseDTO> updateAboutUs(
            @PathVariable UUID aboutUsUuid,
            @Valid @RequestBody AboutUsUpdateDTO updateDto
    ) {
        log.info("Admin request to update About Us content with UUID: {}. Update DTO: {}", aboutUsUuid, updateDto);
        AboutUs existingAboutUs = aboutUsService.read(aboutUsUuid); // Fetches current entity to ensure it exists and get its internal ID
        log.debug("Found existing About Us entry with ID: {} for UUID: {}", existingAboutUs.getId(), aboutUsUuid);

        AboutUs aboutUsWithUpdates = AboutUsMapper.applyUpdateDtoToEntity(updateDto, existingAboutUs);
        log.debug("Applied DTO updates to AboutUs entity: {}", aboutUsWithUpdates);

        AboutUs persistedAboutUs = aboutUsService.update(aboutUsWithUpdates); // Service.update takes the full entity
        log.info("Successfully updated About Us entry with ID: {} and UUID: {}", persistedAboutUs.getId(), persistedAboutUs.getUuid());
        return ResponseEntity.ok(AboutUsMapper.toDto(persistedAboutUs));
    }

    /**
     * Retrieves all "About Us" entries.
     * This can be used if multiple "About Us" entries or historical versions are maintained.
     * For admin purposes, this might include all entries, regardless of a soft-delete status,
     * depending on the `getAll()` service implementation.
     *
     * @return A ResponseEntity containing a list of {@link AboutUsResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<AboutUsResponseDTO>> getAllAboutUsEntries() {
        log.info("Admin request to get all About Us entries.");
        List<AboutUs> aboutUsList = aboutUsService.getAll();
        if (aboutUsList.isEmpty()) {
            log.info("No About Us entries found.");
            return ResponseEntity.noContent().build();
        }
        List<AboutUsResponseDTO> dtoList = AboutUsMapper.toDtoList(aboutUsList);
        log.info("Successfully retrieved {} About Us entries.", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Soft-deletes an "About Us" entry identified by its UUID.
     * The controller first retrieves the entity by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param aboutUsUuid The UUID of the "About Us" entry to delete.
     * @return A ResponseEntity with no content if successful, or not found if the entry doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the "About Us" entry with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{aboutUsUuid}")
    public ResponseEntity<Void> deleteAboutUs(@PathVariable UUID aboutUsUuid) {
        log.info("Admin request to delete About Us content with UUID: {}", aboutUsUuid);
        AboutUs aboutUsToDelete = aboutUsService.read(aboutUsUuid); // Fetch entity to get its internal ID
        log.debug("Found About Us entry with ID: {} (UUID: {}) for deletion.", aboutUsToDelete.getId(), aboutUsUuid);

        boolean deleted = aboutUsService.delete(aboutUsToDelete.getId()); // Service method uses internal ID
        if (!deleted) {
            // This condition might be reached if the service's delete method returns false for reasons other than "not found"
            // (e.g., business rule violation, or if it was already deleted and the method doesn't re-confirm).
            // If service.delete(id) is expected to throw if ID doesn't exist after the initial read,
            // this block might signify a different issue.
            log.warn("About Us entry with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", aboutUsToDelete.getId(), aboutUsUuid);
            return ResponseEntity.notFound().build(); // Or another appropriate status
        }
        log.info("Successfully soft-deleted About Us entry with ID: {} (UUID: {}).", aboutUsToDelete.getId(), aboutUsUuid);
        return ResponseEntity.noContent().build();
    }


}