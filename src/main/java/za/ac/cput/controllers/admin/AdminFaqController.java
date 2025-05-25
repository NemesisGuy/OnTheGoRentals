package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.request.FaqCreateDTO;
import za.ac.cput.domain.dto.request.FaqUpdateDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.domain.mapper.FaqMapper;
import za.ac.cput.service.IFaqService; // Inject interface
import za.ac.cput.exception.ResourceNotFoundException; // For explicit error handling

import java.util.List; // Correct import
import java.util.UUID;

/**
 * AdminFaqController.java
 * Controller for Admin to manage FAQ entries.
 * Author: Aqeel Hanslo (219374422) // Updated by: [Your Name]
 * Date: 08 August 2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/admin/faqs") // Standardized base path
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminFaqController {

    private final IFaqService faqService;

    @Autowired
    public AdminFaqController(IFaqService faqService) {
        this.faqService = faqService;
    }

    /**
     * Admin creates a new FAQ.
     */
    @PostMapping // POST /api/v1/admin/faqs
    public ResponseEntity<FaqResponseDTO> createFaq(@Valid @RequestBody FaqCreateDTO faqCreateDTO) {
        Faq faqToCreate = FaqMapper.toEntity(faqCreateDTO); // Map DTO to entity
        Faq createdFaqEntity = faqService.create(faqToCreate); // Service takes entity
        FaqResponseDTO responseDto = FaqMapper.toDto(createdFaqEntity); // Map result to DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Admin retrieves a specific FAQ by its UUID.
     */
    @GetMapping("/{faqUuid}") // GET /api/v1/admin/faqs/{uuid_value}
    public ResponseEntity<FaqResponseDTO> getFaqByUuid(@PathVariable UUID faqUuid) {
        Faq faqEntity = faqService.read(faqUuid); // Service returns entity
        // Service should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(FaqMapper.toDto(faqEntity));
    }

    /**
     * Admin updates an existing FAQ identified by its UUID.
     * Your original PUT was missing an identifier in the path.
     */
    @PutMapping("/{faqUuid}") // PUT /api/v1/admin/faqs/{uuid_value}
    public ResponseEntity<FaqResponseDTO> updateFaq(
            @PathVariable UUID faqUuid,
            @Valid @RequestBody FaqUpdateDTO faqUpdateDTO
    ) {
        Faq existingFaq = faqService.read(faqUuid); // Fetch current entity state
        // Service should throw ResourceNotFoundException if not found

        // Mapper creates a new entity instance with updates applied
        Faq faqWithUpdates = FaqMapper.applyUpdateDtoToEntity(faqUpdateDTO, existingFaq);

        // Service's update method receives this new instance with the same ID.
        Faq persistedUpdatedFaq = faqService.update(faqWithUpdates);

        return ResponseEntity.ok(FaqMapper.toDto(persistedUpdatedFaq));
    }

    /**
     * Admin retrieves all FAQ entries.
     * Service method `getAllAdminView()` might include soft-deleted ones.
     */
    @GetMapping // GET /api/v1/admin/faqs (replaces /get-all)
    public ResponseEntity<List<FaqResponseDTO>> getAllFaqsForAdmin() {
        List<Faq> faqs = faqService.getAll(); // Service returns List<Faq> entities
        List<FaqResponseDTO> faqDTOs = FaqMapper.toDtoList(faqs);
        if (faqDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(faqDTOs);
    }

    /**
     * Admin soft-deletes an FAQ by its UUID.
     */
    @DeleteMapping("/{faqUuid}") // DELETE /api/v1/admin/faqs/{uuid_value}
    public ResponseEntity<Void> deleteFaq(@PathVariable UUID faqUuid) {
        Faq faq = faqService.read(faqUuid); // Service returns entity, throws ResourceNotFoundException if not found
        boolean deleted = faqService.delete(faq.getId()); // Service handles logic
        // If service throws ResourceNotFoundException, a @ControllerAdvice would handle it.
        // If service returns false for "not found":
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Your original /read/{id} and /delete/{id} (integer based) are removed
    // in favor of UUID-based endpoints for the admin API.
    // If you need to keep them for internal use, they should have distinct paths.
}