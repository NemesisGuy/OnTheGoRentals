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
import za.ac.cput.domain.dto.request.FeedbackCreateDTO;
import za.ac.cput.domain.dto.response.FeedbackResponseDTO;
import za.ac.cput.domain.entity.Feedback;
import za.ac.cput.domain.mapper.FeedbackMapper;
import za.ac.cput.service.IFeedbackService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * FeedbackController.java
 * Controller for managing Feedback entities. Primarily allows public users to submit feedback.
 * Also includes endpoints for administrative review of feedback.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/feedback")
@Tag(name = "Feedback Management", description = "Endpoints for submitting and managing user feedback.")
public class FeedbackController {

    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);
    private final IFeedbackService feedbackService;

    /**
     * Constructs a FeedbackController with the necessary Feedback service.
     *
     * @param feedbackService The service implementation for feedback operations.
     */
    @Autowired
    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
        log.info("FeedbackController initialized.");
    }

    /**
     * Creates a new feedback submission. This endpoint is intended for public access.
     *
     * @param feedbackCreateDTO The DTO containing data for the new feedback.
     * @return A ResponseEntity containing the created feedback DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Submit new feedback", description = "Allows any user (guest or authenticated) to submit new feedback.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback submitted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedbackResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid feedback data provided")
    })
    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> createFeedback(
            @Valid @RequestBody FeedbackCreateDTO feedbackCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create new feedback with DTO: {}", requesterId, feedbackCreateDTO);

        Feedback feedbackToCreate = FeedbackMapper.toEntity(feedbackCreateDTO);
        Feedback createdFeedbackEntity = feedbackService.create(feedbackToCreate);

        log.info("Requester [{}]: Successfully created feedback with UUID: {}", requesterId, createdFeedbackEntity.getUuid());
        return new ResponseEntity<>(FeedbackMapper.toDto(createdFeedbackEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves all non-deleted feedback submissions. This operation should be restricted to administrators.
     *
     * @return A ResponseEntity containing a list of feedback DTOs, or 204 No Content if none exist.
     */
    @Operation(summary = "Get all feedback (Admin)", description = "Retrieves all feedback submissions. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved feedback list"),
            @ApiResponse(responseCode = "204", description = "No feedback submissions found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to view feedback")
    })
    @GetMapping
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedback() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all feedback.", requesterId);

        List<Feedback> feedbackList = feedbackService.getAll();
        if (feedbackList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FeedbackMapper.toDtoList(feedbackList));
    }

    /**
     * Retrieves a specific feedback entry by its UUID. This operation should be restricted to administrators.
     *
     * @param feedbackUuid The UUID of the feedback to retrieve.
     * @return A ResponseEntity containing the feedback DTO if found.
     */
    @Operation(summary = "Get feedback by UUID (Admin)", description = "Retrieves a specific feedback entry by its UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback found", content = @Content(schema = @Schema(implementation = FeedbackResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized to view this feedback"),
            @ApiResponse(responseCode = "404", description = "Feedback not found with the specified UUID")
    })
    @GetMapping("/{feedbackUuid}")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackByUuid(
            @Parameter(description = "UUID of the feedback to retrieve", required = true) @PathVariable UUID feedbackUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get feedback by UUID: {}", requesterId, feedbackUuid);

        Feedback feedbackEntity = feedbackService.read(feedbackUuid); // Throws ResourceNotFoundException
        return ResponseEntity.ok(FeedbackMapper.toDto(feedbackEntity));
    }

    /**
     * Soft-deletes a feedback entry by its UUID. This operation should be restricted to administrators.
     *
     * @param feedbackUuid The UUID of the feedback to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete feedback by UUID (Admin)", description = "Soft-deletes a feedback entry by its UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feedback deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete feedback"),
            @ApiResponse(responseCode = "404", description = "Feedback not found with the specified UUID")
    })
    @DeleteMapping("/{feedbackUuid}")
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(description = "UUID of the feedback to delete", required = true) @PathVariable UUID feedbackUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("ADMIN ACTION: Requester [{}] attempting to delete feedback with UUID: {}", requesterId, feedbackUuid);

        Feedback feedbackToDelete = feedbackService.read(feedbackUuid);
        feedbackService.delete(feedbackToDelete.getId());

        log.info("Requester [{}]: Successfully soft-deleted feedback with UUID: {}.", requesterId, feedbackUuid);
        return ResponseEntity.noContent().build();
    }
}