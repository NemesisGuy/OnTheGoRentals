package za.ac.cput.controllers; // Or a more specific package

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.HelpCenter; // Service works with this
import za.ac.cput.domain.dto.request.HelpCenterCreateDTO;
import za.ac.cput.domain.dto.request.HelpCenterUpdateDTO;
import za.ac.cput.domain.dto.response.HelpCenterResponseDTO;
import za.ac.cput.domain.mapper.HelpCenterMapper;
import za.ac.cput.service.IHelpCenterService; // Inject interface

import java.util.List;
import java.util.UUID;

/**
 * HelpCenterController.java
 * Controller for Help Center topics/articles.
 * Author: Aqeel Hanslo (219374422) // Updated by: [Your Name]
 * Date: 29 August 2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/help-topics") // New RESTful base path
// @CrossOrigin(...) // Prefer global CORS configuration
public class HelpCenterController {

    private final IHelpCenterService helpCenterService; // Inject interface

    @Autowired
    public HelpCenterController(IHelpCenterService helpCenterService) {
        this.helpCenterService = helpCenterService;
    }

    /**
     * Retrieves all non-deleted help topics, optionally filtered by category.
     * This is a public endpoint.
     * @param category (Optional) The category to filter by.
     * @return ResponseEntity with a list of HelpCenterResponseDTOs.
     */
    @GetMapping // GET /api/v1/help-topics  OR GET /api/v1/help-topics?category=some_category
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllHelpTopics(
            @RequestParam(required = false) String category
    ) {
        List<HelpCenter> helpTopics;
        if (category != null && !category.trim().isEmpty()) {
            helpTopics = helpCenterService.findByCategory(category);
        } else {
            helpTopics = helpCenterService.getAll();
        }

        List<HelpCenterResponseDTO> dtoList = HelpCenterMapper.toDtoList(helpTopics);
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves a specific help topic by its UUID.
     * This is a public endpoint.
     * @param topicUuid The UUID of the help topic to retrieve.
     * @return ResponseEntity with HelpCenterResponseDTO or 404 if not found.
     */
    @GetMapping("/{topicUuid}") // GET /api/v1/help-topics/{uuid_value}
    public ResponseEntity<HelpCenterResponseDTO> getHelpTopicByUuid(@PathVariable UUID topicUuid) {
        HelpCenter helpTopicEntity = helpCenterService.read(topicUuid);
        // Service method should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(HelpCenterMapper.toDto(helpTopicEntity));
    }


    // --- Admin Endpoints (Example - these would typically require ADMIN role) ---

    /**
     * Creates a new help topic. (Typically an Admin operation)
     * @param createDto DTO containing data for the new help topic.
     * @return ResponseEntity with the created HelpCenterResponseDTO and HTTP status 201.
     */
    @PostMapping // POST /api/v1/help-topics (or move to /admin/help-topics)
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HelpCenterResponseDTO> createHelpTopic(@Valid @RequestBody HelpCenterCreateDTO createDto) {
        HelpCenter topicToCreate = HelpCenterMapper.toEntity(createDto);
        HelpCenter createdEntity = helpCenterService.create(topicToCreate);
        HelpCenterResponseDTO responseDto = HelpCenterMapper.toDto(createdEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing help topic by its UUID. (Typically an Admin operation)
     * @param topicUuid The UUID of the help topic to update.
     * @param updateDto DTO containing the fields to update.
     * @return ResponseEntity with the updated HelpCenterResponseDTO or 404 if not found.
     */
    @PutMapping("/{topicUuid}") // PUT /api/v1/help-topics/{uuid_value}
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HelpCenterResponseDTO> updateHelpTopic(
            @PathVariable UUID topicUuid,
            @Valid @RequestBody HelpCenterUpdateDTO updateDto
    ) {
        HelpCenter existingTopic = helpCenterService.read(topicUuid); // Fetches current state
        HelpCenter topicWithUpdates = HelpCenterMapper.applyUpdateDtoToEntity(updateDto, existingTopic); // Applies DTO changes
        HelpCenter persistedTopic = helpCenterService.update(topicWithUpdates); // Service saves the new state
        return ResponseEntity.ok(HelpCenterMapper.toDto(persistedTopic));
    }

    /**
     * Soft-deletes a help topic by its UUID. (Typically an Admin operation)
     * @param topicUuid The UUID of the help topic to delete.
     * @return ResponseEntity with status 204 No Content or 404 if not found.
     */
    @DeleteMapping("/{topicUuid}") // DELETE /api/v1/help-topics/{uuid_value}
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHelpTopic(@PathVariable UUID topicUuid) {
        // Service's delete method should handle soft deletion logic
        HelpCenter helpCenter = helpCenterService.read(topicUuid);
        boolean deleted = helpCenterService.delete(helpCenter.getId());
        // Service's softDeleteByUuid should throw ResourceNotFoundException if not found for more consistent error handling,
        // or controller checks boolean as done here.
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Your original /category/{category} endpoint is now handled by the @GetMapping with @RequestParam
    // If you strongly prefer /category/{category} as a path:
    /*
    @GetMapping("/category/{category}")
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllByCategoryFromPath(@PathVariable String category) {
        List<HelpCenter> filteredList = helpCenterService.getAllNonDeletedByCategory(category);
        List<HelpCenterResponseDTO> dtoList = HelpCenterMapper.toDtoList(filteredList);
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }
    */
}