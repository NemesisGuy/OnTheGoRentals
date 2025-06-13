package za.ac.cput.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IFeedbackService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * FeedbackController.java
 * Controller for managing Feedback entities. Primarily allows public users to submit feedback.
 * Includes endpoints that are typically for administrative review or management of feedback,
 * which should be secured accordingly or moved to an AdminFeedbackController.
 * <p>
 * Author: Peter Buckingham
 * Date: 2025-05-15
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/feedback")
// @CrossOrigin(...) // Prefer global CORS configuration
@Api(value = "Feedback Management", tags = "Feedback Management")
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
     * Creates a new feedback submission.
     * This endpoint is intended for public access.
     *
     * @param feedbackCreateDTO The {@link FeedbackCreateDTO} containing data for the new feedback.
     * @return A ResponseEntity containing the created {@link FeedbackResponseDTO} and HTTP status 201 Created.
     */
    @PostMapping
    @ApiOperation(value = "Submit new feedback", notes = "Allows users to submit new feedback. Intended for public access.")
    public ResponseEntity<FeedbackResponseDTO> createFeedback(
            @ApiParam(value = "Feedback submission data", required = true) @Valid @RequestBody FeedbackCreateDTO feedbackCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create new feedback with DTO: {}", requesterId, feedbackCreateDTO);

        Feedback feedbackToCreate = FeedbackMapper.toEntity(feedbackCreateDTO);
        log.debug("Requester [{}]: Mapped DTO to Feedback entity for creation: {}", requesterId, feedbackToCreate);

        Feedback createdFeedbackEntity = feedbackService.create(feedbackToCreate);
        log.info("Requester [{}]: Successfully created feedback with ID: {} and UUID: {}",
                requesterId, createdFeedbackEntity.getId(), createdFeedbackEntity.getUuid());
        FeedbackResponseDTO responseDto = FeedbackMapper.toDto(createdFeedbackEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves all non-deleted feedback submissions.
     * Access to this endpoint should be considered (e.g., admin-only or public if feedback is shared).
     * For this example, it's treated as potentially admin-viewable.
     *
     * @return A ResponseEntity containing a list of {@link FeedbackResponseDTO}s, or 204 No Content if none exist.
     */
    @GetMapping
    @ApiOperation(value = "Get all feedback", notes = "Retrieves all non-deleted feedback submissions. Access should be configured (e.g., admin-only).")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example if admin-only
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedback() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all feedback.", requesterId);
        // Add authorization check here if needed for non-public access

        List<Feedback> feedbackList = feedbackService.getAll();
        if (feedbackList.isEmpty()) {
            log.info("Requester [{}]: No feedback submissions found.", requesterId);
            return ResponseEntity.noContent().build();
        }
        List<FeedbackResponseDTO> feedbackDTOs = FeedbackMapper.toDtoList(feedbackList);
        log.info("Requester [{}]: Successfully retrieved {} feedback submissions.", requesterId, feedbackDTOs.size());
        return ResponseEntity.ok(feedbackDTOs);
    }

    // --- Endpoints typically requiring Admin role ---

    /**
     * Retrieves a specific feedback entry by its UUID.
     * This operation is typically restricted to administrators.
     *
     * @param feedbackUuid The UUID of the feedback to retrieve.
     * @return A ResponseEntity containing the {@link FeedbackResponseDTO} if found.
     * @throws ResourceNotFoundException if the feedback with the given UUID is not found (handled by service).
     */
    @GetMapping("/{feedbackUuid}")
    @ApiOperation(value = "Get feedback by UUID", notes = "Retrieves a specific feedback entry by its UUID. Typically restricted to administrators.")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<FeedbackResponseDTO> getFeedbackByUuid(
            @ApiParam(value = "UUID of the feedback to retrieve", required = true) @PathVariable UUID feedbackUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get feedback by UUID: {}", requesterId, feedbackUuid);
        // Add authorization check here

        // feedbackService.read(UUID) is expected to throw ResourceNotFoundException if not found.
        Feedback feedbackEntity = feedbackService.read(feedbackUuid);
        log.info("Requester [{}]: Successfully retrieved feedback with ID: {} for UUID: {}",
                requesterId, feedbackEntity.getId(), feedbackEntity.getUuid());
        return ResponseEntity.ok(FeedbackMapper.toDto(feedbackEntity));
    }

    // Update for Feedback is commented out as it's generally not a user action.
    // If admins can edit feedback, uncomment and secure appropriately.
    /*
    @PutMapping("/{feedbackUuid}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<FeedbackResponseDTO> updateFeedback(
            @PathVariable UUID feedbackUuid,
            @Valid @RequestBody FeedbackUpdateDTO feedbackUpdateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update feedback UUID: {} with DTO: {}", requesterId, feedbackUuid, feedbackUpdateDTO);
        // ... (Implementation similar to FaqController's update)
    }
    */

    /**
     * Soft-deletes a feedback entry by its UUID.
     * This operation is typically restricted to administrators.
     * The controller first retrieves the feedback by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param feedbackUuid The UUID of the feedback to delete.
     * @return A ResponseEntity with status 204 No Content if successful, or 404 Not Found.
     * @throws ResourceNotFoundException if the feedback with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{feedbackUuid}")
    @ApiOperation(value = "Delete feedback by UUID", notes = "Soft-deletes a feedback entry by its UUID. Typically restricted to administrators.")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<Void> deleteFeedback(
            @ApiParam(value = "UUID of the feedback to delete", required = true) @PathVariable UUID feedbackUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to delete feedback with UUID: {}", requesterId, feedbackUuid);
        // Add authorization check here

        Feedback feedbackToDelete = feedbackService.read(feedbackUuid);
        log.debug("Requester [{}]: Found feedback ID: {} (UUID: {}) for deletion.",
                requesterId, feedbackToDelete.getId(), feedbackToDelete.getUuid());

        boolean deleted = feedbackService.delete(feedbackToDelete.getId());
        if (!deleted) {
            log.warn("Requester [{}]: Feedback with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.",
                    requesterId, feedbackToDelete.getId(), feedbackToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Requester [{}]: Successfully soft-deleted feedback with ID: {} (UUID: {}).",
                requesterId, feedbackToDelete.getId(), feedbackToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }
}