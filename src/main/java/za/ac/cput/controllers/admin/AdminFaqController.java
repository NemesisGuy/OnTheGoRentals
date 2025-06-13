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
import za.ac.cput.domain.dto.request.FaqCreateDTO;
import za.ac.cput.domain.dto.request.FaqUpdateDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.domain.mapper.FaqMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.service.IFaqService;

import java.util.List;
import java.util.UUID;

/**
 * AdminFaqController.java
 * Controller for administrators to manage FAQ (Frequently Asked Questions) entries.
 * Allows admins to create, retrieve, update, and delete FAQs.
 * External identification of FAQs is by UUID. Internal service operations
 * primarily use integer IDs. This controller bridges that gap.
 * <p>
 * Author: Aqeel Hanslo (219374422)
 * Updated by: System/AI
 * Date: 08 August 2023
 * Updated: [Current Date - e.g., 2024-05-28]
 */
@RestController
@RequestMapping("/api/v1/admin/faqs")
// @CrossOrigin(...) // Prefer global CORS configuration
// @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
@Tag(name = "Admin FAQ Management", description = "Endpoints for administrators to manage FAQ entries.")
public class AdminFaqController {

    private static final Logger log = LoggerFactory.getLogger(AdminFaqController.class);
    private final IFaqService faqService;

    /**
     * Constructs an AdminFaqController with the necessary FAQ service.
     *
     * @param faqService The service implementation for FAQ operations.
     */
    @Autowired
    public AdminFaqController(IFaqService faqService) {
        this.faqService = faqService;
        log.info("AdminFaqController initialized.");
    }

    /**
     * Allows an admin to create a new FAQ.
     *
     * @param faqCreateDTO The {@link FaqCreateDTO} containing the data for the new FAQ.
     * @return A ResponseEntity containing the created {@link FaqResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    @Operation(summary = "Create a new FAQ", description = "Allows an administrator to add a new FAQ to the system.")
    public ResponseEntity<FaqResponseDTO> createFaq(
            @Parameter(description = "Data for the new FAQ", required = true) @Valid @RequestBody FaqCreateDTO faqCreateDTO) {
        log.info("Admin request to create a new FAQ with DTO: {}", faqCreateDTO);
        Faq faqToCreate = FaqMapper.toEntity(faqCreateDTO);
        log.debug("Mapped DTO to Faq entity for creation: {}", faqToCreate);

        Faq createdFaqEntity = faqService.create(faqToCreate);
        log.info("Successfully created FAQ with ID: {} and UUID: {}", createdFaqEntity.getId(), createdFaqEntity.getUuid());
        FaqResponseDTO responseDto = FaqMapper.toDto(createdFaqEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a specific FAQ by its UUID.
     *
     * @param faqUuid The UUID of the FAQ to retrieve.
     * @return A ResponseEntity containing the {@link FaqResponseDTO} if found.
     * @throws ResourceNotFoundException if the FAQ with the given UUID is not found (handled by service).
     */
    @GetMapping("/{faqUuid}")
    @Operation(summary = "Get FAQ by UUID", description = "Retrieves a specific FAQ by its UUID.")
    public ResponseEntity<FaqResponseDTO> getFaqByUuid(
            @Parameter(description = "UUID of the FAQ to retrieve", required = true) @PathVariable UUID faqUuid) {
        log.info("Admin request to get FAQ by UUID: {}", faqUuid);
        Faq faqEntity = faqService.read(faqUuid);
        // The faqService.read(UUID) method is expected to throw ResourceNotFoundException if not found.
        log.info("Successfully retrieved FAQ with ID: {} for UUID: {}", faqEntity.getId(), faqEntity.getUuid());
        return ResponseEntity.ok(FaqMapper.toDto(faqEntity));
    }

    /**
     * Allows an admin to update an existing FAQ identified by its UUID.
     *
     * @param faqUuid      The UUID of the FAQ to update.
     * @param faqUpdateDTO The {@link FaqUpdateDTO} containing the updated data for the FAQ.
     * @return A ResponseEntity containing the updated {@link FaqResponseDTO}.
     * @throws ResourceNotFoundException if the FAQ with the given UUID is not found (handled by service).
     */
    @PutMapping("/{faqUuid}")
    @Operation(summary = "Update an existing FAQ", description = "Allows an administrator to update the details of an existing FAQ.")
    public ResponseEntity<FaqResponseDTO> updateFaq(
            @Parameter(description = "UUID of the FAQ to update", required = true) @PathVariable UUID faqUuid,
            @Parameter(description = "Updated data for the FAQ", required = true) @Valid @RequestBody FaqUpdateDTO faqUpdateDTO
    ) {
        log.info("Admin request to update FAQ with UUID: {}. Update DTO: {}", faqUuid, faqUpdateDTO);
        Faq existingFaq = faqService.read(faqUuid);
        log.debug("Found existing FAQ with ID: {} and UUID: {}", existingFaq.getId(), existingFaq.getUuid());

        Faq faqWithUpdates = FaqMapper.applyUpdateDtoToEntity(faqUpdateDTO, existingFaq);
        log.debug("Applied DTO updates to Faq entity: {}", faqWithUpdates);

        Faq persistedUpdatedFaq = faqService.update(faqWithUpdates);
        log.info("Successfully updated FAQ with ID: {} and UUID: {}", persistedUpdatedFaq.getId(), persistedUpdatedFaq.getUuid());
        return ResponseEntity.ok(FaqMapper.toDto(persistedUpdatedFaq));
    }

    /**
     * Retrieves all FAQ entries for administrative view.
     * Depending on the service implementation, this might include FAQs
     * that have been soft-deleted.
     *
     * @return A ResponseEntity containing a list of {@link FaqResponseDTO}s, or no content if none exist.
     */
    @GetMapping
    @Operation(summary = "Get all FAQs (Admin)", description = "Retrieves a list of all FAQs for administrative view.")
    public ResponseEntity<List<FaqResponseDTO>> getAllFaqsForAdmin() {
        log.info("Admin request to get all FAQs.");
        List<Faq> faqs = faqService.getAll();
        if (faqs.isEmpty()) {
            log.info("No FAQs found.");
            return ResponseEntity.noContent().build();
        }
        List<FaqResponseDTO> faqDTOs = FaqMapper.toDtoList(faqs);
        log.info("Successfully retrieved {} FAQs.", faqDTOs.size());
        return ResponseEntity.ok(faqDTOs);
    }

    /**
     * Allows an admin to soft-delete an FAQ by its UUID.
     * The controller first retrieves the FAQ by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param faqUuid The UUID of the FAQ to delete.
     * @return A ResponseEntity with no content if successful, or not found if the FAQ doesn't exist or couldn't be deleted.
     * @throws ResourceNotFoundException if the FAQ with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{faqUuid}")
    @Operation(summary = "Delete an FAQ", description = "Allows an administrator to soft-delete an FAQ by its UUID.")
    public ResponseEntity<Void> deleteFaq(
            @Parameter(description = "UUID of the FAQ to delete", required = true) @PathVariable UUID faqUuid) {
        log.info("Admin request to delete FAQ with UUID: {}", faqUuid);
        Faq faqToDelete = faqService.read(faqUuid);
        log.debug("Found FAQ with ID: {} (UUID: {}) for deletion.", faqToDelete.getId(), faqToDelete.getUuid());

        boolean deleted = faqService.delete(faqToDelete.getId());
        if (!deleted) {
            log.warn("FAQ with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.", faqToDelete.getId(), faqToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Successfully soft-deleted FAQ with ID: {} (UUID: {}).", faqToDelete.getId(), faqToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }
}