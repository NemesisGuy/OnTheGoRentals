package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.HelpCenterCreateDTO;
import za.ac.cput.domain.dto.request.HelpCenterUpdateDTO;
import za.ac.cput.domain.dto.response.HelpCenterResponseDTO;
import za.ac.cput.domain.entity.HelpCenter;
import za.ac.cput.domain.mapper.HelpCenterMapper;
import za.ac.cput.service.IHelpCenterService;
import za.ac.cput.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * AdminHelpCenterController.java
 * Controller for administrators to manage Help Center topics or articles.
 * Allows admins to create, retrieve, update, and delete help topics.
 * External identification of help topics is by UUID. Internal service operations
 * primarily use integer IDs. This controller bridges that gap.
 *
 * Author: Aqeel Hanslo (219374422)
 * Updated by: System/AI
 * Date: 08 August 2023
 * Updated: [Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/help-topics")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminHelpCenterController {

    private static final Logger log = LoggerFactory.getLogger(AdminHelpCenterController.class);
    private final IHelpCenterService helpCenterService;

    /**
     * Constructs an AdminHelpCenterController with the necessary HelpCenter service.
     *
     * @param helpCenterService The service implementation for Help Center operations.
     */
    @Autowired
    public AdminHelpCenterController(IHelpCenterService helpCenterService) {
        this.helpCenterService = helpCenterService;
        log.info("AdminHelpCenterController initialized.");
    }

    /**
     * Allows an admin to create a new help topic.
     *
     * @param createDto The {@link HelpCenterCreateDTO} containing the data for the new help topic.
     * @return A ResponseEntity containing the created {@link HelpCenterResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<HelpCenterResponseDTO> createHelpTopic(@Valid @RequestBody HelpCenterCreateDTO createDto) {
        log.info("Admin request to create a new help topic with DTO: {}", createDto);
        HelpCenter topicToCreate = HelpCenterMapper.toEntity(createDto);
        log.debug("Mapped DTO to HelpCenter entity for creation: {}", topicToCreate);

        HelpCenter createdEntity = helpCenterService.create(topicToCreate);
        log.info("Successfully created help topic with ID: {} and UUID: {}", createdEntity.getId(), createdEntity.getUuid());
        HelpCenterResponseDTO responseDto = HelpCenterMapper.toDto(createdEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific help topic by its UUID.
     *
     * @param topicUuid The UUID of the help topic to retrieve.
     * @return A ResponseEntity containing the {@link HelpCenterResponseDTO} if found.
     * @throws ResourceNotFoundException if the help topic with the given UUID is not found (handled by service).
     */
    @GetMapping("/{topicUuid}")
    public ResponseEntity<HelpCenterResponseDTO> getHelpTopicByUuid(@PathVariable UUID topicUuid) {
        log.info("Admin request to get help topic by UUID: {}", topicUuid);
        HelpCenter topicEntity = helpCenterService.read(topicUuid);
        // The helpCenterService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved help topic with ID: {} for UUID: {}", topicEntity.getId(), topicEntity.getUuid());
        return ResponseEntity.ok(HelpCenterMapper.toDto(topicEntity));
    }

    /**
     * Allows an admin to update an existing help topic identified by its UUID.
     *
     * @param topicUuid The UUID of the help topic to update.
     * @param updateDto The {@link HelpCenterUpdateDTO} containing the updated data for the help topic.
     * @return A ResponseEntity containing the updated {@link HelpCenterResponseDTO}.
     * @throws ResourceNotFoundException if the help topic with the given UUID is not found (handled by service).
     */
    @PutMapping("/{topicUuid}")
    public ResponseEntity<HelpCenterResponseDTO> updateHelpTopic(
            @PathVariable UUID topicUuid,
            @Valid @RequestBody HelpCenterUpdateDTO updateDto
    ) {
        log.info("Admin request to update help topic with UUID: {}. Update DTO: {}", topicUuid, updateDto);
        HelpCenter existingTopic = helpCenterService.read(topicUuid);
        log.debug("Found existing help topic with ID: {} and UUID: {}", existingTopic.getId(), existingTopic.getUuid());

        HelpCenter topicWithUpdates = HelpCenterMapper.applyUpdateDtoToEntity(updateDto, existingTopic);
        log.debug("Applied DTO updates to HelpCenter entity: {}", topicWithUpdates);

        HelpCenter persistedTopic = helpCenterService.update(topicWithUpdates);
        log.info("Successfully updated help topic with ID: {} and UUID: {}", persistedTopic.getId(), persistedTopic.getUuid());
        return ResponseEntity.ok(HelpCenterMapper.toDto(persistedTopic));
    }

    /**
     * Retrieves all help topic entries for administrative view.
     * Depending on the service implementation, this might include topics
     * that have been soft-deleted.
     *
     * @return A ResponseEntity containing a list of {@link HelpCenterResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllHelpTopicsForAdmin() {
        log.info("Admin request to get all help topics.");
        List<HelpCenter> topics = helpCenterService.getAll();
        if (topics.isEmpty()) {
            log.info("No help topics found.");
            return ResponseEntity.noContent().build();
        }
        List<HelpCenterResponseDTO> dtoList = HelpCenterMapper.toDtoList(topics);
        log.info("Successfully retrieved {} help topics.", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Allows an admin to soft-delete a help topic by its UUID.
     * The controller first retrieves the help topic by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param topicUuid The UUID of the help topic to delete.
     * @return A ResponseEntity with no content if successful, or not found if the topic doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the help topic with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{topicUuid}")
    public ResponseEntity<Void> deleteHelpTopic(@PathVariable UUID topicUuid) {
        log.info("Admin request to delete help topic with UUID: {}", topicUuid);
        HelpCenter topicToDelete = helpCenterService.read(topicUuid);
        log.debug("Found help topic with ID: {} (UUID: {}) for deletion.", topicToDelete.getId(), topicToDelete.getUuid());

        boolean deleted = helpCenterService.delete(topicToDelete.getId());
        if (!deleted) {
            log.warn("Help topic with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", topicToDelete.getId(), topicToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully soft-deleted help topic with ID: {} (UUID: {}).", topicToDelete.getId(), topicToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }
}