package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.Feedback; // Service works with this
import za.ac.cput.domain.dto.request.FeedbackCreateDTO;
// import za.ac.cput.domain.dto.request.FeedbackUpdateDTO; // If you implement update
import za.ac.cput.domain.dto.response.FeedbackResponseDTO;
import za.ac.cput.domain.mapper.FeedbackMapper;
import za.ac.cput.service.IFeedbackService; // Inject interface

import java.util.List;
import java.util.UUID;

/**
 * FeedbackController.java
 * Controller for managing Feedback entities.
 * Author: Peter Buckingham // Updated by: [Your Name]
 * Date: 2025-05-15 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/feedback") // Updated base path
// @CrossOrigin(...) // Prefer global CORS
public class FeedbackController {

    private final IFeedbackService feedbackService;

    @Autowired
    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Creates a new feedback submission.
     * This endpoint is typically public.
     * @param feedbackCreateDTO DTO containing data for the new feedback.
     * @return ResponseEntity with the created FeedbackResponseDTO and HTTP status 201.
     */
    @PostMapping // POST /api/v1/feedback
    public ResponseEntity<FeedbackResponseDTO> createFeedback(@Valid @RequestBody FeedbackCreateDTO feedbackCreateDTO) {
        Feedback feedbackToCreate = FeedbackMapper.toEntity(feedbackCreateDTO);
        Feedback createdFeedbackEntity = feedbackService.create(feedbackToCreate);
        FeedbackResponseDTO responseDto = FeedbackMapper.toDto(createdFeedbackEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves all non-deleted feedback submissions.
     * This might be an admin endpoint or a public one if feedback is public.
     * For this example, assuming it's for general viewing (could be admin).
     * @return ResponseEntity with a list of FeedbackResponseDTOs.
     */
    @GetMapping // GET /api/v1/feedback
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAll(); // Or getAll() if it means non-deleted
        List<FeedbackResponseDTO> feedbackDTOs = FeedbackMapper.toDtoList(feedbackList);
        if (feedbackDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(feedbackDTOs);
    }

    // --- Admin-typical Endpoints (Consider moving to an AdminFeedbackController or securing appropriately) ---

    /**
     * Retrieves a specific feedback entry by its UUID. (Typically Admin)
     * @param feedbackUuid The UUID of the feedback to retrieve.
     * @return ResponseEntity with FeedbackResponseDTO or 404 if not found.
     */
    @GetMapping("/{feedbackUuid}") // GET /api/v1/feedback/{uuid_value}
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackByUuid(@PathVariable UUID feedbackUuid) {
        Feedback feedbackEntity = feedbackService.read(feedbackUuid);
        return ResponseEntity.ok(FeedbackMapper.toDto(feedbackEntity));
    }

    // Feedback is generally not updated once submitted by a user.
    // If admins can edit it (e.g., correct typos, categorize), an update endpoint would be here.
    /*
    @PutMapping("/{feedbackUuid}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackResponseDTO> updateFeedback(
            @PathVariable UUID feedbackUuid,
            @Valid @RequestBody FeedbackUpdateDTO feedbackUpdateDTO) {
        Feedback existingFeedback = feedbackService.readByUuid(feedbackUuid);
        Feedback feedbackWithUpdates = FeedbackMapper.applyUpdateDtoToEntity(feedbackUpdateDTO, existingFeedback);
        Feedback persistedFeedback = feedbackService.update(feedbackWithUpdates); // Assumes service.update(Feedback)
        return ResponseEntity.ok(FeedbackMapper.toDto(persistedFeedback));
    }
    */

    /**
     * Soft-deletes a feedback entry by its UUID. (Typically Admin)
     * @param feedbackUuid The UUID of the feedback to delete.
     * @return ResponseEntity with status 204 No Content or 404 if not found.
     */
    @DeleteMapping("/{feedbackUuid}") // DELETE /api/v1/feedback/{uuid_value}
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFeedback(@PathVariable UUID feedbackUuid) {
        Feedback feedback = feedbackService.read(feedbackUuid);
        boolean deleted = feedbackService.delete(feedback.getId());
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    // ----- Keeping your original integer ID endpoints commented out for reference -----
    // These would typically be phased out or only used for very specific internal cases
    // if the public API is moving to UUIDs.

    /*
    @GetMapping("/read/{id}") // Original using Integer ID
    public ResponseEntity<FeedbackResponseDTO> read(@PathVariable Integer id) {
        Feedback feedback = feedbackService.read(id);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(FeedbackMapper.toDto(feedback));
    }

    @PutMapping("/{id}") // Original using Integer ID and Entity in request
    public ResponseEntity<FeedbackResponseDTO> update(@PathVariable Integer id, @RequestBody Feedback feedbackFromRequest) {
        Feedback existingFeedback = feedbackService.read(id);
        if (existingFeedback == null) {
            return ResponseEntity.notFound().build();
        }
        // Apply changes from feedbackFromRequest to existingFeedback
        existingFeedback.setName(feedbackFromRequest.getName());
        existingFeedback.setComment(feedbackFromRequest.getComment());
        // Ensure ID from path is used, not from body if they could mismatch
        existingFeedback.setId(id);


        Feedback updated = feedbackService.update(existingFeedback); // Assumes service.update(Feedback)
        return ResponseEntity.ok(FeedbackMapper.toDto(updated));
    }

    @DeleteMapping("/{id}") // Original using Integer ID
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = feedbackService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    */
}