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
import za.ac.cput.domain.dto.request.AdminContactUsUpdateDTO;
import za.ac.cput.domain.dto.request.ContactUsCreateDTO;
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;
import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.domain.mapper.ContactUsMapper;
import za.ac.cput.service.IContactUsService;

import java.util.List;
import java.util.UUID;

/**
 * AdminContactUsController.java
 * Controller for administrators to manage "Contact Us" submissions.
 * Allows admins to retrieve, update, and delete existing contact submissions.
 *
 * @author Cwenga Dlova (214310671)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/contact-us-submissions")
@Tag(name = "Admin: Contact Us Management", description = "Endpoints for administrators to manage 'Contact Us' submissions.")
public class AdminContactUsController {

    private static final Logger log = LoggerFactory.getLogger(AdminContactUsController.class);
    private final IContactUsService contactUsService;

    /**
     * Constructs an AdminContactUsController with the necessary ContactUs service.
     *
     * @param contactUsService The service implementation for "Contact Us" submission operations.
     */
    @Autowired
    public AdminContactUsController(IContactUsService contactUsService) {
        this.contactUsService = contactUsService;
        log.info("AdminContactUsController initialized.");
    }

    /**
     * Retrieves all contact submissions for administrative view.
     *
     * @return A ResponseEntity containing a list of contact submission DTOs.
     */
    @Operation(summary = "Get all contact submissions", description = "Retrieves all 'Contact Us' submissions for administrative review.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "204", description = "No contact submissions found")
    })
    @GetMapping
    public ResponseEntity<List<ContactUsResponseDTO>> getAllContactSubmissions() {
        log.info("Admin request to get all contact us submissions.");
        List<ContactUs> submissions = contactUsService.getAll();
        if (submissions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ContactUsMapper.toDtoList(submissions));
    }

    /**
     * Retrieves a specific contact submission by its UUID.
     *
     * @param submissionUuid The UUID of the contact submission to retrieve.
     * @return A ResponseEntity containing the contact submission DTO.
     */
    @Operation(summary = "Get contact submission by UUID", description = "Retrieves a specific 'Contact Us' submission by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission found", content = @Content(schema = @Schema(implementation = ContactUsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Submission not found with the specified UUID")
    })
    @GetMapping("/{submissionUuid}")
    public ResponseEntity<ContactUsResponseDTO> getContactSubmissionByUuid(
            @Parameter(description = "UUID of the contact submission to retrieve", required = true) @PathVariable UUID submissionUuid) {
        log.info("Admin request to get contact us submission by UUID: {}", submissionUuid);
        ContactUs submissionEntity = contactUsService.read(submissionUuid);
        return ResponseEntity.ok(ContactUsMapper.toDto(submissionEntity));
    }

    /**
     * Allows an admin to create a new contact submission. This is for exceptional cases,
     * as submissions typically come from the public-facing controller.
     *
     * @param createDto The DTO containing the data for the new submission.
     * @return A ResponseEntity containing the created contact submission DTO.
     */
    @Operation(summary = "Create a contact submission (Admin)", description = "Allows an administrator to manually create a new contact submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Submission created successfully", content = @Content(schema = @Schema(implementation = ContactUsResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided")
    })
    @PostMapping
    public ResponseEntity<ContactUsResponseDTO> createContactSubmission(
            @Valid @RequestBody ContactUsCreateDTO createDto) {
        log.info("Admin request to create a new contact us submission with DTO: {}", createDto);
        ContactUs contactUsToCreate = ContactUsMapper.toEntity(createDto);
        ContactUs createdSubmission = contactUsService.create(contactUsToCreate);
        log.info("Successfully created contact us submission with UUID: {}", createdSubmission.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(ContactUsMapper.toDto(createdSubmission));
    }

    /**
     * Allows an admin to update an existing contact submission (e.g., mark as responded).
     *
     * @param submissionUuid The UUID of the contact submission to update.
     * @param updateDto      The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated contact submission DTO.
     */
    @Operation(summary = "Update a contact submission", description = "Allows an administrator to update an existing 'Contact Us' submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission updated successfully", content = @Content(schema = @Schema(implementation = ContactUsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Submission not found with the specified UUID")
    })
    @PutMapping("/{submissionUuid}")
    public ResponseEntity<ContactUsResponseDTO> updateContactSubmission(
            @Parameter(description = "UUID of the contact submission to update", required = true) @PathVariable UUID submissionUuid,
            @Valid @RequestBody AdminContactUsUpdateDTO updateDto) {
        log.info("Admin request to update contact us submission with UUID: {}", submissionUuid);
        ContactUs existingSubmission = contactUsService.read(submissionUuid);
        ContactUs submissionWithUpdates = ContactUsMapper.applyAdminUpdateDtoToEntity(updateDto, existingSubmission);
        ContactUs persistedSubmission = contactUsService.update(submissionWithUpdates);
        log.info("Successfully updated contact us submission with UUID: {}", persistedSubmission.getUuid());
        return ResponseEntity.ok(ContactUsMapper.toDto(persistedSubmission));
    }

    /**
     * Allows an admin to soft-delete a contact submission by its UUID.
     *
     * @param submissionUuid The UUID of the contact submission to delete.
     * @return A ResponseEntity with status 204 No Content.
     */
    @Operation(summary = "Delete a contact submission", description = "Allows an administrator to soft-delete a 'Contact Us' submission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Submission deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Submission not found with the specified UUID")
    })
    @DeleteMapping("/{submissionUuid}")
    public ResponseEntity<Void> deleteContactSubmission(
            @Parameter(description = "UUID of the contact submission to delete", required = true) @PathVariable UUID submissionUuid) {
        log.warn("ADMIN ACTION: Request to delete contact us submission with UUID: {}", submissionUuid);
        ContactUs existingSubmission = contactUsService.read(submissionUuid);
        contactUsService.delete(existingSubmission.getId());
        log.info("Successfully soft-deleted contact us submission with UUID: {}.", submissionUuid);
        return ResponseEntity.noContent().build();
    }
}