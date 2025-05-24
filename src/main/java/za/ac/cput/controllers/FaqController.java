package za.ac.cput.controllers; // Or a more specific package

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Faq; // Service layer works with this
import za.ac.cput.domain.dto.request.FaqCreateDTO;
import za.ac.cput.domain.dto.request.FaqUpdateDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;
import za.ac.cput.domain.mapper.FaqMapper;
import za.ac.cput.service.IFaqService; // Inject interface

import java.util.List;
import java.util.UUID;

/**
 * FaqController.java
 * This is the controller for the Faq entity
 * Author: Aqeel Hanslo (219374422) // Updated by: [Your Name]
 * Date: 29 August 2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/faqs") // Changed base path to be RESTful and versioned
// @CrossOrigin(...) // Prefer global CORS configuration
public class FaqController {

    private final IFaqService faqService; // Inject interface

    @Autowired
    public FaqController(IFaqService faqService) {
        this.faqService = faqService;
    }

    /**
     * Retrieves all non-deleted FAQs.
     * This is a public endpoint.
     * @return ResponseEntity with a list of FaqResponseDTOs.
     */
    @GetMapping // GET /api/v1/faqs (replaces /get-all)
    public ResponseEntity<List<FaqResponseDTO>> getAllFaqs() {
        List<Faq> allFaqs = faqService.getAll(); // Service returns Faq entities
        List<FaqResponseDTO> faqDTOs = FaqMapper.toDtoList(allFaqs);

        if (faqDTOs.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.ok(faqDTOs); // HTTP 200
    }

    /**
     * Retrieves a specific FAQ by its UUID.
     * This is a public endpoint.
     * @param faqUuid The UUID of the FAQ to retrieve.
     * @return ResponseEntity with FaqResponseDTO or 404 if not found.
     */
    @GetMapping("/{faqUuid}") // GET /api/v1/faqs/{uuid_value}
    public ResponseEntity<FaqResponseDTO> getFaqByUuid(@PathVariable UUID faqUuid) {
        Faq faqEntity = faqService.read(faqUuid); // Service returns Faq entity
        // Service method should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(FaqMapper.toDto(faqEntity));
    }

    // --- Admin Endpoints (Example - these would typically require ADMIN role) ---

    /**
     * Creates a new FAQ. (Typically an Admin operation)
     * @param faqCreateDTO DTO containing data for the new FAQ.
     * @return ResponseEntity with the created FaqResponseDTO and HTTP status 201.
     */
    @PostMapping // POST /api/v1/faqs (if this controller is admin-only, or use /admin/faqs)
    // @PreAuthorize("hasRole('ADMIN')") // Example if using Spring Security method protection
    public ResponseEntity<FaqResponseDTO> createFaq(@Valid @RequestBody FaqCreateDTO faqCreateDTO) {
        Faq faqToCreate = FaqMapper.toEntity(faqCreateDTO);
        Faq createdFaqEntity = faqService.create(faqToCreate);
        FaqResponseDTO responseDto = FaqMapper.toDto(createdFaqEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing FAQ identified by its UUID. (Typically an Admin operation)
     * @param faqUuid The UUID of the FAQ to update.
     * @param faqUpdateDTO DTO containing the fields to update.
     * @return ResponseEntity with the updated FaqResponseDTO or 404 if not found.
     */

    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{faqUuid}") // PUT /api/v1/faqs/{uuid_value}
    public ResponseEntity<FaqResponseDTO> updateFaq(
            @PathVariable UUID faqUuid,
            @Valid @RequestBody FaqUpdateDTO faqUpdateDTO
    ) {
        Faq existingFaq = faqService.read(faqUuid); // Fetches current entity

        // Mapper creates a NEW Faq instance with updates applied
        Faq faqWithUpdates = FaqMapper.applyUpdateDtoToEntity(faqUpdateDTO, existingFaq);

        // Service's update method receives this new instance.
        // JPA will treat save() on this as an update to the existing record due to matching ID.
        Faq persistedUpdatedFaq = faqService.update(faqWithUpdates);

        return ResponseEntity.ok(FaqMapper.toDto(persistedUpdatedFaq));
    }




    /**
     * Soft-deletes an FAQ by its UUID. (Typically an Admin operation)
     * @param faqUuid The UUID of the FAQ to delete.
     * @return ResponseEntity with status 204 No Content or 404 if not found.
     */
    @DeleteMapping("/{faqUuid}") // DELETE /api/v1/faqs/{uuid_value}
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaq(@PathVariable UUID faqUuid) {
        Faq faq = faqService.read(faqUuid); // Fetch existing entity
        boolean deleted = faqService.delete(faq.getId()); // Service handles logic, returns true if deleted
        // Service's softDeleteByUuid should throw ResourceNotFoundException if not found,
        // or return false as it does now. If it throws, an exception handler would return 404.
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}