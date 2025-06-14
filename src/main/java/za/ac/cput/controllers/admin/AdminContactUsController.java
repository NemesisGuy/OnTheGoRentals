package za.ac.cput.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.AdminContactUsUpdateDTO;
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;
import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.domain.mapper.ContactUsMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IContactUsService;

import java.util.List;
import java.util.UUID;

/**
 * AdminContactUsController.java
 * Controller for administrators to manage "Contact Us" submissions.
 * Allows admins to retrieve, update, and delete existing contact submissions.
 * Submissions are identified externally by UUIDs. Internal service operations
 * primarily use integer IDs. This controller handles the translation.
 * <p>
 * Author: Cwenga Dlova (214310671)
 * Updated by: System/AI
 * Date: 23/09/2023
 * Updated: [Your Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/contact-us-submissions")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
@Tag(name = "Admin Contact Us Management", description = "Endpoints for administrators to manage 'Contact Us' submissions.")
public class AdminContactUsController {

    private static final Logger log = LoggerFactory.getLogger(AdminContactUsController.class);
    private final IContactUsService contactUsService;

    /**
     * Constructs an AdminContactUsController with the necessary ContactUs service.
     *
     * @param contactUsService The service implementation for "Contact Us" submission operations.
     */
    @Autowired // Standard practice to annotate constructor for injection
    public AdminContactUsController(IContactUsService contactUsService) {
        this.contactUsService = contactUsService;
        log.info("AdminContactUsController initialized.");
    }

    /**
     * Retrieves all contact submissions for administrative view.
     * Depending on the service implementation, this might include submissions
     * regardless of their status (e.g., responded, archived).
     *
     * @return A ResponseEntity containing a list of {@link ContactUsResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    @Operation(summary = "Get all contact submissions", description = "Retrieves all 'Contact Us' submissions for administrative view.")
    public ResponseEntity<List<ContactUsResponseDTO>> getAllContactSubmissions() {
        log.info("Admin request to get all contact us submissions.");
        List<ContactUs> submissions = contactUsService.getAll();
        if (submissions.isEmpty()) {
            log.info("No contact us submissions found.");
            return ResponseEntity.noContent().build();
        }
        List<ContactUsResponseDTO> dtoList = ContactUsMapper.toDtoList(submissions);
        log.info("Successfully retrieved {} contact us submissions.", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves a specific contact submission by its UUID.
     *
     * @param submissionUuid The UUID of the contact submission to retrieve.
     * @return A ResponseEntity containing the {@link ContactUsResponseDTO} if found.
     * @throws ResourceNotFoundException if the submission with the given UUID is not found (handled by service).
     */
    @GetMapping("/{submissionUuid}")
    @Operation(summary = "Get contact submission by UUID", description = "Retrieves a specific 'Contact Us' submission by its UUID.")
    public ResponseEntity<ContactUsResponseDTO> getContactSubmissionByUuid(
            @Parameter(description = "UUID of the contact submission to retrieve", required = true) @PathVariable UUID submissionUuid) {
        log.info("Admin request to get contact us submission by UUID: {}", submissionUuid);
        ContactUs submissionEntity = contactUsService.read(submissionUuid);
        // The contactUsService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved contact us submission with ID: {} for UUID: {}", submissionEntity.getId(), submissionUuid);
        return ResponseEntity.ok(ContactUsMapper.toDto(submissionEntity));
    }

    /**
     * Allows an admin to update an existing contact submission.
     * This could be used to mark a submission as responded to, archive it, or correct minor details.
     *
     * @param submissionUuid The UUID of the contact submission to update.
     * @param updateDto      The {@link AdminContactUsUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link ContactUsResponseDTO}.
     * @throws ResourceNotFoundException if the submission with the given UUID is not found (handled by service).
     */
    @PutMapping("/{submissionUuid}")
    @Operation(summary = "Update a contact submission", description = "Allows an administrator to update an existing 'Contact Us' submission (e.g., mark as responded).")
    public ResponseEntity<ContactUsResponseDTO> updateContactSubmission(
            @Parameter(description = "UUID of the contact submission to update", required = true) @PathVariable UUID submissionUuid,
            @Parameter(description = "Data for updating the contact submission", required = true) @Valid @RequestBody AdminContactUsUpdateDTO updateDto
    ) {
        log.info("Admin request to update contact us submission with UUID: {}. Update DTO: {}", submissionUuid, updateDto);
        ContactUs existingSubmission = contactUsService.read(submissionUuid);
        log.debug("Found existing contact us submission with ID: {} for UUID: {}", existingSubmission.getId(), submissionUuid);

        ContactUs submissionWithUpdates = ContactUsMapper.applyAdminUpdateDtoToEntity(updateDto, existingSubmission);
        log.debug("Applied DTO updates to ContactUs entity: {}", submissionWithUpdates);

        ContactUs persistedSubmission = contactUsService.update(submissionWithUpdates);
        // Assuming 'getSubmissionId()' or 'getUuid()' is the method in ContactUs entity to get its UUID. Adjust if different.
        log.info("Successfully updated contact us submission with ID: {} and UUID: {}", persistedSubmission.getId(), persistedSubmission.getUuid()); // Or getUuid()
        return ResponseEntity.ok(ContactUsMapper.toDto(persistedSubmission));
    }

    /**
     * Allows an admin to soft-delete a contact submission by its UUID.
     * The controller first retrieves the submission by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param submissionUuid The UUID of the contact submission to delete.
     * @return A ResponseEntity with no content if successful, or not found if the submission doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the submission with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{submissionUuid}")
    @Operation(summary = "Delete a contact submission", description = "Allows an administrator to soft-delete a 'Contact Us' submission by its UUID.")
    public ResponseEntity<Void> deleteContactSubmission(
            @Parameter(description = "UUID of the contact submission to delete", required = true) @PathVariable UUID submissionUuid) {
        log.info("Admin request to delete contact us submission with UUID: {}", submissionUuid);
        ContactUs existingSubmission = contactUsService.read(submissionUuid);
        log.debug("Found contact us submission with ID: {} (UUID: {}) for deletion.", existingSubmission.getId(), submissionUuid);

        boolean deleted = contactUsService.delete(existingSubmission.getId());
        if (!deleted) {
            log.warn("Contact us submission with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", existingSubmission.getId(), submissionUuid);
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully soft-deleted contact us submission with ID: {} (UUID: {}).", existingSubmission.getId(), submissionUuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Allows an admin to create a new contact submission.
     * Note: This endpoint is typically not used by admins, as contact submissions usually
     * originate from public users via a different controller. It is provided here for completeness
     * if such a specific admin use case exists.
     * The request body directly takes a {@link ContactUs} entity, which is generally discouraged
     * in favor of using a DTO (e.g., AdminContactUsCreateDTO).
     *
     * @param submission The {@link ContactUs} entity to create. It's recommended to use a DTO (e.g., AdminContactUsCreateDTO) instead of direct entity exposure.
     * @return A ResponseEntity containing the created {@link ContactUsResponseDTO} and HTTP status CREATED.
     */
    /// ToDo: Consider using a specific DTO for creation to avoid direct entity exposure.
    @PostMapping()
    @Operation(summary = "Create a contact submission (Admin - Special Case)",
               description = "Allows an admin to create a new contact submission. Note: This endpoint takes a direct ContactUs entity, which is generally discouraged; using a DTO is recommended. This is for completeness if a specific admin use case exists.")
    public ResponseEntity<ContactUsResponseDTO> createContactSubmission(
            @Parameter(description = "ContactUs entity to create. Using a DTO is recommended.", required = true) @Valid @RequestBody ContactUs submission) {
        log.info("Admin request to create a new contact us submission with entity: {}", submission);
        if (submission.getUuid() != null) { // Or getUuid()
            log.warn("Attempting to create a contact submission with a pre-existing UUID: {}. This UUID will likely be overridden by the persistence layer.", submission.getUuid());
        }
        // It's highly recommended to use a CreateDTO and map it to the ContactUs entity here.
        // Example: ContactUs entityToCreate = ContactUsMapper.toEntity(adminCreateDto);
        ContactUs createdSubmission = contactUsService.create(submission);
        log.info("Successfully created contact us submission with ID: {} and UUID: {}", createdSubmission.getId(), createdSubmission.getUuid()); // Or getUuid()
        return ResponseEntity.status(HttpStatus.CREATED).body(ContactUsMapper.toDto(createdSubmission));
    }
}