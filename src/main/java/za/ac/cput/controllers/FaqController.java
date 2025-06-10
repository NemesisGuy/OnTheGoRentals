package za.ac.cput.controllers;

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
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * FaqController.java
 * This controller manages FAQ (Frequently Asked Questions) entries.
 * It provides public endpoints for retrieving FAQs and potentially administrative
 * endpoints for creating, updating, and deleting FAQs.
 * For production, CRUD operations (POST, PUT, DELETE) should be secured
 * and likely moved to a dedicated AdminFaqController.
 * <p>
 * Author: Aqeel Hanslo (219374422)
 * Date: 29 August 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/faqs")
// @CrossOrigin(...) // Prefer global CORS configuration
public class FaqController {

    private static final Logger log = LoggerFactory.getLogger(FaqController.class);
    private final IFaqService faqService;

    /**
     * Constructs an FaqController with the necessary FAQ service.
     *
     * @param faqService The service implementation for FAQ operations.
     */
    @Autowired
    public FaqController(IFaqService faqService) {
        this.faqService = faqService;
        log.info("FaqController initialized.");
    }

    /**
     * Retrieves all non-deleted FAQs.
     * This endpoint is intended for public access.
     *
     * @return A ResponseEntity containing a list of {@link FaqResponseDTO}s, or 204 No Content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<FaqResponseDTO>> getAllFaqs() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all FAQs.", requesterId);

        List<Faq> allFaqs = faqService.getAll();
        if (allFaqs.isEmpty()) {
            log.info("Requester [{}]: No FAQs found.", requesterId);
            return ResponseEntity.noContent().build();
        }
        List<FaqResponseDTO> faqDTOs = FaqMapper.toDtoList(allFaqs);
        log.info("Requester [{}]: Successfully retrieved {} FAQs.", requesterId, faqDTOs.size());
        return ResponseEntity.ok(faqDTOs);
    }

    /**
     * Retrieves a specific FAQ by its UUID.
     * This endpoint is intended for public access.
     *
     * @param faqUuid The UUID of the FAQ to retrieve.
     * @return A ResponseEntity containing the {@link FaqResponseDTO} if found.
     * @throws ResourceNotFoundException if the FAQ with the given UUID is not found (handled by service).
     */
    @GetMapping("/{faqUuid}")
    public ResponseEntity<FaqResponseDTO> getFaqByUuid(@PathVariable UUID faqUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get FAQ by UUID: {}", requesterId, faqUuid);

        // faqService.read(UUID) is expected to throw ResourceNotFoundException if not found.
        Faq faqEntity = faqService.read(faqUuid);
        log.info("Requester [{}]: Successfully retrieved FAQ with ID: {} for UUID: {}",
                requesterId, faqEntity.getId(), faqEntity.getUuid());
        return ResponseEntity.ok(FaqMapper.toDto(faqEntity));
    }

    // --- Endpoints typically requiring Admin role ---
    // For production, these should be secured (e.g., @PreAuthorize("hasRole('ADMIN')"))
    // or moved to a separate AdminFaqController under an /admin path.

    /**
     * Creates a new FAQ.
     * This operation is typically restricted to administrators.
     *
     * @param faqCreateDTO The {@link FaqCreateDTO} containing data for the new FAQ.
     * @return A ResponseEntity containing the created {@link FaqResponseDTO} and HTTP status 201 Created.
     */
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<FaqResponseDTO> createFaq(@Valid @RequestBody FaqCreateDTO faqCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new FAQ with DTO: {}", requesterId, faqCreateDTO);
        // Add authorization check here if not using @PreAuthorize
        // if (!isRequesterAdmin(requesterId)) {
        //     log.warn("Requester [{}]: Attempted to create FAQ without admin privileges.", requesterId);
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }

        Faq faqToCreate = FaqMapper.toEntity(faqCreateDTO);
        log.debug("Requester [{}]: Mapped DTO to Faq entity for creation: {}", requesterId, faqToCreate);

        Faq createdFaqEntity = faqService.create(faqToCreate);
        log.info("Requester [{}]: Successfully created FAQ with ID: {} and UUID: {}",
                requesterId, createdFaqEntity.getId(), createdFaqEntity.getUuid());
        FaqResponseDTO responseDto = FaqMapper.toDto(createdFaqEntity);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing FAQ identified by its UUID.
     * This operation is typically restricted to administrators.
     *
     * @param faqUuid      The UUID of the FAQ to update.
     * @param faqUpdateDTO The {@link FaqUpdateDTO} containing the fields to update.
     * @return A ResponseEntity containing the updated {@link FaqResponseDTO}.
     * @throws ResourceNotFoundException if the FAQ with the given UUID is not found (handled by service).
     */
    @PutMapping("/{faqUuid}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<FaqResponseDTO> updateFaq(
            @PathVariable UUID faqUuid,
            @Valid @RequestBody FaqUpdateDTO faqUpdateDTO
    ) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update FAQ with UUID: {}. Update DTO: {}",
                requesterId, faqUuid, faqUpdateDTO);
        // Add authorization check here if not using @PreAuthorize

        Faq existingFaq = faqService.read(faqUuid);
        log.debug("Requester [{}]: Found existing FAQ ID: {}, UUID: {} for update.",
                requesterId, existingFaq.getId(), existingFaq.getUuid());

        Faq faqWithUpdates = FaqMapper.applyUpdateDtoToEntity(faqUpdateDTO, existingFaq);
        log.debug("Requester [{}]: Mapped DTO to update Faq entity: {}", requesterId, faqWithUpdates);

        Faq persistedUpdatedFaq = faqService.update(faqWithUpdates);
        log.info("Requester [{}]: Successfully updated FAQ with ID: {} and UUID: {}",
                requesterId, persistedUpdatedFaq.getId(), persistedUpdatedFaq.getUuid());
        return ResponseEntity.ok(FaqMapper.toDto(persistedUpdatedFaq));
    }

    /**
     * Soft-deletes an FAQ by its UUID.
     * This operation is typically restricted to administrators.
     * The controller first retrieves the FAQ by UUID to obtain its internal integer ID,
     * which is then passed to the service's delete method.
     *
     * @param faqUuid The UUID of the FAQ to delete.
     * @return A ResponseEntity with status 204 No Content if successful, or 404 Not Found.
     * @throws ResourceNotFoundException if the FAQ with the given UUID is not found (when reading it).
     */
    @DeleteMapping("/{faqUuid}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')") // Example security
    public ResponseEntity<Void> deleteFaq(@PathVariable UUID faqUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to delete FAQ with UUID: {}", requesterId, faqUuid);
        // Add authorization check here if not using @PreAuthorize

        Faq faqToDelete = faqService.read(faqUuid);
        log.debug("Requester [{}]: Found FAQ ID: {} (UUID: {}) for deletion.",
                requesterId, faqToDelete.getId(), faqToDelete.getUuid());

        boolean deleted = faqService.delete(faqToDelete.getId());
        if (!deleted) {
            log.warn("Requester [{}]: FAQ with ID: {} (UUID: {}) could not be deleted by service, or was already marked as deleted.",
                    requesterId, faqToDelete.getId(), faqToDelete.getUuid());
            return ResponseEntity.notFound().build();
        }
        log.info("Requester [{}]: Successfully soft-deleted FAQ with ID: {} (UUID: {}).",
                requesterId, faqToDelete.getId(), faqToDelete.getUuid());
        return ResponseEntity.noContent().build();
    }
}