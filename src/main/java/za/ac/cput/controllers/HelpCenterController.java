package za.ac.cput.controllers;

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
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * HelpCenterController.java
 * This controller manages Help Center topics/articles (FAQs).
 * It provides public endpoints for retrieving help topics and administrative endpoints for CRUD operations.
 *
 * @author Aqeel Hanslo (219374422)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/help-topics")
@Tag(name = "Help Center Management", description = "Endpoints for viewing and managing Help Center topics (FAQs).")
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
     * Retrieves all non-deleted help topics, optionally filtered by category. This is a public endpoint.
     *
     * @param category (Optional) The category name to filter help topics by.
     * @return A ResponseEntity containing a list of help topic DTOs, or 204 No Content if none match.
     */
    @Operation(summary = "Get all help topics", description = "Retrieves all publicly visible help topics, with an option to filter by category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved help topics"),
            @ApiResponse(responseCode = "204", description = "No help topics found for the given criteria")
    })
    @GetMapping
    public ResponseEntity<List<HelpCenterResponseDTO>> getAllHelpTopics(
            @Parameter(description = "Optional category name to filter topics by.") @RequestParam(required = false) String category) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        List<HelpCenter> helpTopics;
        if (category != null && !category.trim().isEmpty()) {
            log.info("Requester [{}]: Request to get help topics filtered by category: {}", requesterId, category);
            helpTopics = helpCenterService.findByCategory(category);
        } else {
            log.info("Requester [{}]: Request to get all help topics.", requesterId);
            helpTopics = helpCenterService.getAll();
        }

        if (helpTopics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(HelpCenterMapper.toDtoList(helpTopics));
    }

    /**
     * Retrieves a specific help topic by its UUID. This is a public endpoint.
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
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get help topic by UUID: {}", requesterId, topicUuid);

        HelpCenter helpTopicEntity = helpCenterService.read(topicUuid);
        return ResponseEntity.ok(HelpCenterMapper.toDto(helpTopicEntity));
    }

    /**
     * Creates a new help topic. This operation should be restricted to administrators.
     *
     * @param createDto The DTO containing data for the new help topic.
     * @return A ResponseEntity containing the created help topic DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Create a new help topic (Admin)", description = "Creates a new help topic. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Help topic created successfully", content = @Content(schema = @Schema(implementation = HelpCenterResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "403", description = "User not authorized to create help topics")
    })
    @PostMapping
    public ResponseEntity<HelpCenterResponseDTO> createHelpTopic(
            @Valid @RequestBody HelpCenterCreateDTO createDto) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new help topic with DTO: {}", requesterId, createDto);

        HelpCenter topicToCreate = HelpCenterMapper.toEntity(createDto);
        HelpCenter createdEntity = helpCenterService.create(topicToCreate);

        log.info("Requester [{}]: Successfully created help topic with UUID: {}", requesterId, createdEntity.getUuid());
        return new ResponseEntity<>(HelpCenterMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Updates an existing help topic by its UUID. This operation should be restricted to administrators.
     *
     * @param topicUuid The UUID of the help topic to update.
     * @param updateDto The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated help topic DTO.
     */
    @Operation(summary = "Update an existing help topic (Admin)", description = "Updates an existing help topic by its UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Help topic updated successfully", content = @Content(schema = @Schema(implementation = HelpCenterResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update help topics"),
            @ApiResponse(responseCode = "404", description = "Help topic not found with the specified UUID")
    })
    @PutMapping("/{topicUuid}")
    public ResponseEntity<HelpCenterResponseDTO> updateHelpTopic(
            @Parameter(description = "UUID of the help topic to update", required = true) @PathVariable UUID topicUuid,
            @Valid @RequestBody HelpCenterUpdateDTO updateDto) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update help topic with UUID: {}", requesterId, topicUuid);

        HelpCenter existingTopic = helpCenterService.read(topicUuid);
        HelpCenter topicWithUpdates = HelpCenterMapper.applyUpdateDtoToEntity(updateDto, existingTopic);
        HelpCenter updatedTopic = helpCenterService.update(topicWithUpdates);

        log.info("Requester [{}]: Successfully updated help topic with UUID: {}", requesterId, updatedTopic.getUuid());
        return ResponseEntity.ok(HelpCenterMapper.toDto(updatedTopic));
    }

    /**
     * Soft-deletes a help topic by its UUID. This operation should be restricted to administrators.
     *
     * @param topicUuid The UUID of the help topic to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete a help topic by UUID (Admin)", description = "Soft-deletes a help topic by its UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Help topic deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete help topics"),
            @ApiResponse(responseCode = "404", description = "Help topic not found with the specified UUID")
    })
    @DeleteMapping("/{topicUuid}")
    public ResponseEntity<Void> deleteHelpTopic(
            @Parameter(description = "UUID of the help topic to delete", required = true) @PathVariable UUID topicUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("ADMIN ACTION: Requester [{}] attempting to delete help topic with UUID: {}", requesterId, topicUuid);

        HelpCenter helpTopicToDelete = helpCenterService.read(topicUuid);
        helpCenterService.delete(helpTopicToDelete.getId());

        log.info("Requester [{}]: Successfully soft-deleted help topic with UUID: {}.", requesterId, topicUuid);
        return ResponseEntity.noContent().build();
    }
}