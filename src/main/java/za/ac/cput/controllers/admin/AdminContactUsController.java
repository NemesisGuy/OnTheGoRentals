package za.ac.cput.controllers.admin;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.ContactUs; // Service works with this
import za.ac.cput.domain.dto.request.AdminContactUsUpdateDTO; // Using specific admin update DTO
import za.ac.cput.domain.dto.response.ContactUsResponseDTO;
import za.ac.cput.domain.mapper.ContactUsMapper;
import za.ac.cput.service.IContactUsService; // Inject interface

import java.util.List;
import java.util.UUID;

/**
 * AdminContactUsController.java
 * Controller for Admin to manage Contact Us submissions.
 * Author: Cwenga Dlova (214310671) // Updated by: [Your Name]
 * Date: 23/09/2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/admin/contact-us-submissions") // More descriptive resource name
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminContactUsController {

    private final IContactUsService contactUsService;


    public AdminContactUsController(IContactUsService contactUsService) {
        this.contactUsService = contactUsService;
    }

    /**
     * Retrieves all contact submissions for administrative view.
     * Might include soft-deleted ones if service method `getAllAdmin()` supports it.
     */
    @GetMapping
    public ResponseEntity<List<ContactUsResponseDTO>> getAllContactSubmissions() {
        List<ContactUs> submissions = contactUsService.getAll(); // Service returns entities
        if (submissions.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no submissions found
        }
        return ResponseEntity.ok(ContactUsMapper.toDtoList(submissions));
    }

    /**
     * Retrieves a specific contact submission by its UUID.
     */
    @GetMapping("/{submissionUuid}")
    public ResponseEntity<ContactUsResponseDTO> getContactSubmissionByUuid(@PathVariable UUID submissionUuid) {
        ContactUs submissionEntity = contactUsService.read(submissionUuid);
        // Service should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(ContactUsMapper.toDto(submissionEntity));
    }

    /**
     * Admin updates an existing contact submission (e.g., to mark as responded, correct typos).
     */
    @PutMapping("/{submissionUuid}")
    public ResponseEntity<ContactUsResponseDTO> updateContactSubmission(
            @PathVariable UUID submissionUuid,
            @Valid @RequestBody AdminContactUsUpdateDTO updateDto
    ) {
        ContactUs existingSubmission = contactUsService.read(submissionUuid); // Fetches current state
        // Service's readByUuid should throw ResourceNotFoundException if not found

        ContactUs submissionWithUpdates = ContactUsMapper.applyAdminUpdateDtoToEntity(updateDto, existingSubmission); // Applies DTO
        ContactUs persistedSubmission = contactUsService.update(submissionWithUpdates); // Service saves new state

        return ResponseEntity.ok(ContactUsMapper.toDto(persistedSubmission));
    }

    /**
     * Admin soft-deletes a contact submission by its UUID.
     */
    @DeleteMapping("/{submissionUuid}")
    public ResponseEntity<Void> deleteContactSubmission(@PathVariable UUID submissionUuid) {
        ContactUs existingSubmission = contactUsService.read(submissionUuid);
        boolean deleted = contactUsService.delete(existingSubmission.getId());
        // Service's softDeleteByUuid should throw ResourceNotFoundException if not found,
        // or controller checks boolean.
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // The POST /create for contactUs is typically a public endpoint in ContactUsController.
    // Admins usually manage/view/delete existing submissions rather than creating new ones "as an admin".
    // If an admin *does* need to create one, it would look similar to other admin create methods,
    // taking an AdminContactUsCreateDTO (if different from public one) and mapping to entity.
    /**
     * Admin creates a new contact submission (if needed, typically public users do this).
     * This is usually not required for admin controllers.
     * If needed, you can implement it similarly to other create methods.
     */
    @PostMapping()
    public ResponseEntity<ContactUsResponseDTO> createContactSubmission(@Valid @RequestBody ContactUs submission) {
        // This is typically not an admin action, but if needed:
        ContactUs createdSubmission = contactUsService.create(submission);
        return ResponseEntity.status(201).body(ContactUsMapper.toDto(createdSubmission));
    }
}