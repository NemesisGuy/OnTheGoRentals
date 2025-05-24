package za.ac.cput.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.ContactUs; // Service will work with this
import za.ac.cput.domain.dto.request.ContactUsRequestDTO; // Request DTO
import za.ac.cput.domain.dto.request.ContactUsRequestDTO;
import za.ac.cput.domain.dto.response.ContactUsResponseDTO; // Response DTO
import za.ac.cput.domain.mapper.ContactUsMapper;
import za.ac.cput.service.IContactUsService; // Inject interface

@RestController
@RequestMapping("/api/v1/contact-us") // More RESTful base path
// @CrossOrigin(...) // Prefer global CORS configuration
public class ContactUsController {

    private final IContactUsService contactUsService; // Inject interface

    @Autowired
    public ContactUsController(IContactUsService contactUsService) {
        this.contactUsService = contactUsService;
    }

    /**
     * Creates a new contact submission.
     *
     * @param contactUsCreateDTO The DTO containing the contact form data.
     * @return The created ContactUs submission as a DTO.
     */
    @PostMapping // POST to the collection URI /api/v1/contact-us
    public ResponseEntity<ContactUsResponseDTO> createContactSubmission(
            @Valid @RequestBody ContactUsRequestDTO contactUsCreateDTO
    ) {
        // 1. Controller receives the DTO
        // 2. Map DTO to Entity (or pass DTO to service if service handles mapping)
        ContactUs contactUsToCreate = ContactUsMapper.toEntity(contactUsCreateDTO);

        // 3. Call service with the Entity
        // The service method 'create' should accept and return the ContactUs entity.
        ContactUs createdContactUsEntity = contactUsService.create(contactUsToCreate);

        // 4. Map the saved Entity back to a Response DTO
        ContactUsResponseDTO responseDto = ContactUsMapper.toDto(createdContactUsEntity);

        // 5. Return 201 Created status with the response DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // If you need other endpoints for ContactUs (e.g., for admins to view or delete them):
    // Example: GET /api/v1/admin/contact-us (in an AdminContactUsController)
    // Example: GET /api/v1/admin/contact-us/{contactUsUuid} (in an AdminContactUsController)
    // Example: DELETE /api/v1/admin/contact-us/{contactUsUuid} (in an AdminContactUsController)
}