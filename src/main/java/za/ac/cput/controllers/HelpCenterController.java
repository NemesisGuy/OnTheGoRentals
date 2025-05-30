package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.HelpCenter;
import za.ac.cput.domain.dto.request.HelpCenterCreateDTO;
import za.ac.cput.domain.dto.request.HelpCenterUpdateDTO;
import za.ac.cput.domain.dto.response.HelpCenterResponseDTO;
import za.ac.cput.domain.mapper.HelpCenterMapper;
import za.ac.cput.exception.ResourceNotFoundException; // For consistency
import za.ac.cput.service.IHelpCenterService;
import za.ac.cput.utils.SecurityUtils; // Import your helper

import java.util.List;
import java.util.UUID;

/**
 * HelpCenterController.java
 * Controller for managing Help Center topics/articles.
 * Provides public endpoints for retrieving help topics and potentially administrative
 * endpoints for creating, updating, and deleting them.
 * CRUD operations (POST, PUT, DELETE) should be secured for admin-only access
 * or moved to a dedicated AdminHelpCenterController.
 *
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/help-topics")
// @CrossOrigin(...) // Prefer global CORS configuration
public class HelpCenterController {

    private static final Logger log = LoggerFactory.getLogger(HelpCenterController.class);
    private final IHelpCenterService helpCenterService;

    /**
     * Constructs a HelpCenterController with the necessary HelpCenter service.
     *
     * @param helpCenterService The service implementation for Help Center operations.
     */
    @Autowired
    public HelpCenterController(IHelpCenterService helpCenterService) {
        this.helpCenterService = helpCenterService;
        log.info("HelpCenterController initialized.");
    }

    /**
     * Retrieves all non-deleted help topics, optionally filtered by category.
     * This endpoint is intended for public access.
     *
     * @param category (Optional) The category name to filter help topics by. Case-sensitive.
     * @return A ResponseEntity containing a list of {@link HelpCenterResponseDTO}s, or 204 No Content if none match.
     */
    @GetMapping
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllHelpTopics(
            @RequestParam(required = false) String category
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        List<HelpCenter> helpTopics;
        if (category != null && !category.trim().isEmpty()) {
            log.info("Requester [{}]: Request to get all help topics filtered by category: {}", requesterId, category);
            helpTopics = helpCenterService.findByCategory(category);
        } else {
            log.info("Requester [{}]: Request to get all help topics.", requesterId);
            helpTopics = helpCenterService.getAll();
        }

        if (helpTopics.isEmpty()) {
            log.info("Requester [{}]: No help topics found for the given criteria (Category: {}).",
                    requesterId, category != null ? category : "None");
            return ResponseEntity.noContent().build();
        }
        List<HelpCenterResponseDTO> dtoList = HelpCenterMapper.toDtoList(helpTopics);
        log.info("Requester [{}]: Successfully retrieved {} help topics (Category: {}).",
                requesterId, dtoList.size(), category != null ? category : "All");
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves a specific help topic by its UUID.
     * This endpoint is intended for public access.
     *
     * @param topicUuid The UUID of the help topic to retrieve.
     * @return A ResponseEntity containing the {@link HelpCenterResponseDTO} if found.
     * @throws ResourceNotFoundException if the help topic with the given UUID is not found (handled by service).
     */
    @GetMapping("/{topicUuid}")
    public ResponseEntity<HelpCenterResponseDTO> getHelpTopicByUuid(@PathVariable UUID topicUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get help topic by UUID: {}", requesterId, topicUuid);

        // helpCenterService.read(UUID) is expected to throw ResourceNotFoundException if not found.
        HelpCenter helpTopicEntity = helpCenterService.read(topicUuid);
        log.info("Requester [{}]: Successfully retrieved help topic with ID: {} for UUID: {}",
                requesterId, helpTopicEntity.getId(), helpTopicEntity.getUuid());
        return ResponseEntity.ok(HelpCenterMapper.toDto(helpTopicEntity));
    }

    // --- Endpoints typically requiring Admin role ---
    // For production, these should be secured or moved to a separate AdminHelpCenterController.

    /**
     * Creates a new help topic.
     * This operation is typically restricted to administrators.
     *
     * @param createDto The {@link HelpCenterCreateDTO} containing data for the new help topic.
     * @return A ResponseEntity containing the created {@link HelpCenterResponseDTO} and HTTP status 201 Created.
     */
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<HelpCenterResponseDTO> createHelpTopic(@Valid @RequestBody HelpCenterCreateDTO createDto) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new help topic with DTO: {}", requesterId, createDto);
        // Add authorization check here

        HelpCenter topicToCreate = HelpCenterMapper.toEntity(createDto);
        log.debug("Requester [{}]: Mapped DTO to HelpCenter entity for creation: {}", requesterId, topicToCreate);

        HelpCenter createdEntity = helpCenterService.create(topicToCreate);
        log.info("Requester [{}]: Successfully created help topic with ID: {} and UUID: {}",
                requesterId, createdEntity.getId(), createdEntity.getUuid());
        HelpCenterResponseDTO responseDto = HelpCenterMapper.toDto(createdEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing help topic by its UUID.
     * This operation is typically restricted to administrators.
     *
     * @param topicUuid The UUID of the help topic to update.
     * @param updateDto The {@link HelpCenterUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link HelpCenterResponseDTO}.
     * @throws ResourceNotFoundException if the help topic with the given UUID is not found (handled by service).
     */
    @PutMapping("/{topicUuid}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<HelpCenterResponseDTO> updateHelpTopic(
            @PathVariable UUID topicUuid,
            @Valid @RequestBody HelpCenterUpdateDTO updateDto
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update help topic with UUID: {}. Update DTO: {}",
                requesterId, topicUuid, updateDto);
        // Add authorization check here

        HelpCenter existingTopic = helpCenterService.read(topicUuid);
        log.debug("Requester [{}]: Found existing help topic ID: {}, UUID: {} for update.",
                requesterId, existingTopic.getId(), existingTopic.getUuid());

        HelpCenter topicWithUpdates = HelpCenterMapper.applyUpdateDtoToEntity(updateDto, existingTopic);
        log.debug("Requester [{}]: Mapped DTO to update HelpCenter entity: {}", requesterId, topicWithUpdates);

        HelpCenter persistedTopic = helpCenterService.update(topicWithUpdates);
        log.info("Requester [{}]: Successfully updated help topic with ID: {} and UUID: {}",
                requesterId, persistedTopic.getId(), persistedTopic.getUuid());
        return ResponseEntity.ok(HelpCenterMapper.toDto(persistedTopic));
    }

    /**
     * Soft-deletes a help topic by its UUID.
     * This operation is typically restricted to administrators.
     * The controller first retrieves the help topic by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param topicUuid The UUID of the help topic to delete.
     * @return A ResponseEntity with status 204 No Content if successful, or 404 Not Found.
     * @throws ResourceNotFoundException if the help topic with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{topicUuid}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<Void> deleteHelpTopic(@PathVariable UUID topicUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to delete help topic with UUID: {}", requesterId, topicUuid);
        // Add authorization check here

        HelpCenter helpTopicToDelete = helpCenterService.read(topicUuid);
        log.debug("Requester [{}]: Found help topic ID: {} (UUID: {}) for deletion.",
                requesterId, helpTopicToDelete.getId(), helpTopicToDelete.getUuid());

        boolean deleted = helpCenterService.delete(helpTopicToDelete.getId());
        if (!deleted) {
            log.warn("Requester [{}]: Help topic with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.",
                    requesterId, helpTopicToDelete.getId(), helpTopicToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Requester [{}]: Successfully soft-deleted help topic with ID: {} (UUID: {}).",
                requesterId, helpTopicToDelete.getId(), helpTopicToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }
}