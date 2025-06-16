package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;
import java.util.UUID;

/**
 * AdminHelpCenterController.java
 * Controller for administrators to manage Help Center topics or articles.
 * Allows admins to perform full CRUD operations on help topics.
 *
 * @author Aqeel Hanslo (219374422)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/help-topics")
@Tag(name = "Admin: Help Center Management", description = "Endpoints for administrators to manage Help Center topics/articles.")
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
     * Creates a new help topic.
     *
     * @param createDto The DTO containing the data for the new help topic.
     * @return A ResponseEntity containing the created help topic DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Create a new help topic", description = "Allows an administrator to add a new help topic to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Help topic created successfully", content = @Content(schema = @Schema(implementation = HelpCenterResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided")
    })
    @PostMapping
    public ResponseEntity<HelpCenterResponseDTO> createHelpTopic(@Valid @RequestBody HelpCenterCreateDTO createDto) {
        log.info("Admin request to create a new help topic with DTO: {}", createDto);
        HelpCenter topicToCreate = HelpCenterMapper.toEntity(createDto);
        HelpCenter createdEntity = helpCenterService.create(topicToCreate);
        log.info("Successfully created help topic with UUID: {}", createdEntity.getUuid());
        return new ResponseEntity<>(HelpCenterMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves all help topic entries for administrative view.
     *
     * @return A ResponseEntity containing a list of all help topic DTOs.
     */
    @Operation(summary = "Get all help topics (Admin)", description = "Retrieves a list of all help topics for administrative view.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of help topics"),
            @ApiResponse(responseCode = "204", description = "No help topics found")
    })
    @GetMapping
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllHelpTopicsForAdmin() {
        log.info("Admin request to get all help topics.");
        List<HelpCenter> topics = helpCenterService.getAll();
        if (topics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(HelpCenterMapper.toDtoList(topics));
    }

    /**
     * Retrieves a specific help topic by its UUID.
     *
     * @param topicUuid The UUID of the help topic to retrieve.
     * @return A ResponseEntity containing the help topic DTO if found.
     */
    @Operation(summary = "Get help topic by UUID", description = "Retrieves a specific help topic by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Help topic found", content = @Content(schema = @Schema(implementation = HelpCenterResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Help topic not found with the specified UUID")
    })
    @GetMapping("/{topicUuid}")
    public ResponseEntity<HelpCenterResponseDTO> getHelpTopicByUuid(
            @Parameter(description = "UUID of the help topic to retrieve", required = true) @PathVariable UUID topicUuid) {
        log.info("Admin request to get help topic by UUID: {}", topicUuid);
        HelpCenter topicEntity = helpCenterService.read(topicUuid);
        return ResponseEntity.ok(HelpCenterMapper.toDto(topicEntity));
    }

    /**
     * Updates an existing help topic identified by its UUID.
     *
     * @param topicUuid The UUID of the help topic to update.
     * @param updateDto The DTO containing the updated data for the help topic.
     * @return A ResponseEntity containing the updated help topic DTO.
     */
    @Operation(summary = "Update an existing help topic", description = "Updates the details of an existing help topic by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Help topic updated successfully", content = @Content(schema = @Schema(implementation = HelpCenterResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "404", description = "Help topic not found with the specified UUID")
    })
    @PutMapping("/{topicUuid}")
    public ResponseEntity<HelpCenterResponseDTO> updateHelpTopic(
            @Parameter(description = "UUID of the help topic to update", required = true) @PathVariable UUID topicUuid,
            @Valid @RequestBody HelpCenterUpdateDTO updateDto) {
        log.info("Admin request to update help topic with UUID: {}", topicUuid);
        HelpCenter existingTopic = helpCenterService.read(topicUuid);
        HelpCenter topicWithUpdates = HelpCenterMapper.applyUpdateDtoToEntity(updateDto, existingTopic);
        HelpCenter persistedTopic = helpCenterService.update(topicWithUpdates);
        log.info("Successfully updated help topic with UUID: {}", persistedTopic.getUuid());
        return ResponseEntity.ok(HelpCenterMapper.toDto(persistedTopic));
    }

    /**
     * Soft-deletes a help topic by its UUID.
     *
     * @param topicUuid The UUID of the help topic to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete a help topic", description = "Soft-deletes a help topic by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Help topic deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Help topic not found with the specified UUID")
    })
    @DeleteMapping("/{topicUuid}")
    public ResponseEntity<Void> deleteHelpTopic(
            @Parameter(description = "UUID of the help topic to delete", required = true) @PathVariable UUID topicUuid) {
        log.warn("ADMIN ACTION: Request to delete help topic with UUID: {}", topicUuid);
        HelpCenter topicToDelete = helpCenterService.read(topicUuid);
        helpCenterService.delete(topicToDelete.getId());
        log.info("Successfully soft-deleted help topic with UUID: {}.", topicUuid);
        return ResponseEntity.noContent().build();
    }
}