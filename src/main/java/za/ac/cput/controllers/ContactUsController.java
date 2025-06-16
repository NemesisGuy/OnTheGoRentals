package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.dto.request.ContactUsCreateDTO;
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;
import za.ac.cput.domain.entity.ContactUs;
import za.ac.cput.domain.mapper.ContactUsMapper;
import za.ac.cput.service.IContactUsService;
import za.ac.cput.utils.SecurityUtils;

/**
 * ContactUsController.java
 * Controller for handling public "Contact Us" form submissions.
 * Allows any user (guest or authenticated) to submit a contact request.
 *
 * @author Peter Buckingham
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/contact-us")
@Tag(name = "Contact Us", description = "Endpoint for handling public 'Contact Us' form submissions.")
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
     * The data is received as a {@link ContactUsCreateDTO}. This endpoint is publicly accessible.
     *
     * @param contactUsCreateDTO The DTO containing the contact form data (e.g., name, email, message).
     * @return A ResponseEntity containing the created {@link ContactUsResponseDTO} with an HTTP 201 Created status.
     */
    @Operation(summary = "Submit a contact form", description = "Allows any user (guest or authenticated) to submit a contact request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Submission created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactUsResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data provided in the form")
    })
    @PostMapping
    public ResponseEntity<ContactUsResponseDTO> createContactSubmission(
            @Valid @RequestBody ContactUsCreateDTO contactUsCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Received new contact us submission with DTO: {}", requesterId, contactUsCreateDTO);

        // 1. Map DTO to Entity
        ContactUs contactUsToCreate = ContactUsMapper.toEntity(contactUsCreateDTO);
        log.debug("Requester [{}]: Mapped DTO to ContactUs entity for creation: {}", requesterId, contactUsToCreate);

        // 2. Call service with the Entity
        ContactUs createdContactUsEntity = contactUsService.create(contactUsToCreate);
        log.info("Requester [{}]: Successfully created contact us submission with ID: {} and UUID: {}",
                requesterId, createdContactUsEntity.getId(), createdContactUsEntity.getUuid());

        // 3. Map the saved Entity back to a Response DTO
        ContactUsResponseDTO responseDto = ContactUsMapper.toDto(createdContactUsEntity);

        // 4. Return 201 Created status with the response DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}