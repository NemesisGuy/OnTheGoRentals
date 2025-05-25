package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.HelpCenterCreateDTO;
import za.ac.cput.domain.dto.request.HelpCenterUpdateDTO;
import za.ac.cput.domain.dto.response.HelpCenterResponseDTO;
import za.ac.cput.domain.entity.HelpCenter;
import za.ac.cput.domain.mapper.HelpCenterMapper;
import za.ac.cput.service.IHelpCenterService; // Inject interface
import za.ac.cput.exception.ResourceNotFoundException; // For explicit error handling

import java.util.List; // Correct import
import java.util.UUID;

/**
 * AdminHelpCenterController.java
 * Controller for Admin to manage Help Center topics/articles.
 * Author: Aqeel Hanslo (219374422) // Updated by: [Your Name]
 * Date: 08 August 2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/admin/help-topics") // Standardized base path
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminHelpCenterController {

    private final IHelpCenterService helpCenterService;

    @Autowired
    public AdminHelpCenterController(IHelpCenterService helpCenterService) {
        this.helpCenterService = helpCenterService;
    }

    /**
     * Admin creates a new help topic.
     */
    @PostMapping // POST /api/v1/admin/help-topics
    public ResponseEntity<HelpCenterResponseDTO> createHelpTopic(@Valid @RequestBody HelpCenterCreateDTO createDto) {
        HelpCenter topicToCreate = HelpCenterMapper.toEntity(createDto); // Map DTO to entity
        HelpCenter createdEntity = helpCenterService.create(topicToCreate); // Service takes entity
        HelpCenterResponseDTO responseDto = HelpCenterMapper.toDto(createdEntity); // Map result to DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Admin retrieves a specific help topic by its UUID.
     */
    @GetMapping("/{topicUuid}") // GET /api/v1/admin/help-topics/{uuid_value}
    public ResponseEntity<HelpCenterResponseDTO> getHelpTopicByUuid(@PathVariable UUID topicUuid) {
        HelpCenter topicEntity = helpCenterService.read(topicUuid); // Service returns entity
        // Service's readByUuid should throw ResourceNotFoundException if not found.
        return ResponseEntity.ok(HelpCenterMapper.toDto(topicEntity));
    }

    /**
     * Admin updates an existing help topic identified by its UUID.
     * Your original update used POST, changing to PUT for RESTful update.
     */
    @PutMapping("/{topicUuid}") // PUT /api/v1/admin/help-topics/{uuid_value}
    public ResponseEntity<HelpCenterResponseDTO> updateHelpTopic(
            @PathVariable UUID topicUuid,
            @Valid @RequestBody HelpCenterUpdateDTO updateDto
    ) {
        HelpCenter existingTopic = helpCenterService.read(topicUuid); // Fetch current entity state
        // Service's readByUuid should throw ResourceNotFoundException if not found.

        // Mapper creates a new entity instance with updates applied
        HelpCenter topicWithUpdates = HelpCenterMapper.applyUpdateDtoToEntity(updateDto, existingTopic);

        // Service's update method receives this new instance with the same ID.
        HelpCenter persistedTopic = helpCenterService.update(topicWithUpdates);

        return ResponseEntity.ok(HelpCenterMapper.toDto(persistedTopic));
    }

    /**
     * Admin retrieves all help topic entries.
     * Service method `getAllAdminView()` might include soft-deleted ones.
     */
    @GetMapping // GET /api/v1/admin/help-topics (replaces /get-all)
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllHelpTopicsForAdmin() {
        List<HelpCenter> topics = helpCenterService.getAll(); // Service returns List<HelpCenter>
        List<HelpCenterResponseDTO> dtoList = HelpCenterMapper.toDtoList(topics);
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Admin soft-deletes a help topic by its UUID.
     */
    @DeleteMapping("/{topicUuid}") // DELETE /api/v1/admin/help-topics/{uuid_value}
    public ResponseEntity<Void> deleteHelpTopic(@PathVariable UUID topicUuid) {
        HelpCenter existingTopic = helpCenterService.read(topicUuid); // Fetch current entity state
        boolean deleted = helpCenterService.delete(existingTopic.getId()); // Service handles logic
        // If service throws ResourceNotFoundException, a @ControllerAdvice would handle it.
        if (!deleted) {
            return ResponseEntity.notFound().build(); // If service returns false for not found
        }
        return ResponseEntity.noContent().build();
    }

    // Original integer ID based methods are removed in favor of UUIDs for this admin API.
}