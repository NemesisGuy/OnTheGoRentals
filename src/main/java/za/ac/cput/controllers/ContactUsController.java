package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.domain.dto.request.ContactUsCreateDTO;
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;
import za.ac.cput.domain.mapper.ContactUsMapper;
import za.ac.cput.service.IContactUsService;
import za.ac.cput.utils.SecurityUtils; // Import your helper

/**
 * ContactUsController.java
 * Controller for handling public "Contact Us" form submissions.
 * Allows any user (guest or authenticated) to submit a contact request.
 *
 * Author: [Original Author Name - Please specify if known]
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/contact-us") // Public API path for contact submissions
// @CrossOrigin(...) // Prefer global CORS configuration
public class ContactUsController {

    private static final Logger log = LoggerFactory.getLogger(ContactUsController.class);
    private final IContactUsService contactUsService;

    /**
     * Constructs a ContactUsController with the necessary ContactUs service.
     *
     * @param contactUsService The service implementation for "Contact Us" operations.
     */
    @Autowired
    public ContactUsController(IContactUsService contactUsService) {
        this.contactUsService = contactUsService;
        log.info("ContactUsController initialized.");
    }

    /**
     * Creates a new contact submission from the public "Contact Us" form.
     * The data is received as a {@link ContactUsCreateDTO}.
     *
     * @param contactUsCreateDTO The DTO containing the contact form data (e.g., name, email, message).
     * @return A ResponseEntity containing the created {@link ContactUsResponseDTO} with an HTTP 201 Created status.
     */
    @PostMapping
    public ResponseEntity<ContactUsResponseDTO> createContactSubmission(
            @Valid @RequestBody ContactUsCreateDTO contactUsCreateDTO
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Received new contact us submission with DTO: {}", requesterId, contactUsCreateDTO);

        // 1. Map DTO to Entity
        ContactUs contactUsToCreate = ContactUsMapper.toEntity(contactUsCreateDTO);
        log.debug("Requester [{}]: Mapped DTO to ContactUs entity for creation: {}", requesterId, contactUsToCreate);

        // 2. Call service with the Entity
        // The service method 'create' should accept and return the ContactUs entity,
        // handling any necessary backend logic (e.g., setting submission date, generating UUID).
        ContactUs createdContactUsEntity = contactUsService.create(contactUsToCreate);
        log.info("Requester [{}]: Successfully created contact us submission with ID: {} and UUID: {}",
                requesterId, createdContactUsEntity.getId(), createdContactUsEntity.getUuid());

        // 3. Map the saved Entity back to a Response DTO
        ContactUsResponseDTO responseDto = ContactUsMapper.toDto(createdContactUsEntity);

        // 4. Return 201 Created status with the response DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Other endpoints related to ContactUs (e.g., for admins to view, manage, or delete submissions)
    // would typically reside in a separate AdminContactUsController, as noted in your original comments.
    // Example:
    // GET /api/v1/admin/contact-us-submissions (in AdminContactUsController)
    // GET /api/v1/admin/contact-us-submissions/{submissionUuid} (in AdminContactUsController)
}