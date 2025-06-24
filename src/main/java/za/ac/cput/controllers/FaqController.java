package za.ac.cput.controllers;

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
import za.ac.cput.domain.dto.request.FaqCreateDTO;
import za.ac.cput.domain.dto.request.FaqUpdateDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.domain.mapper.FaqMapper;
import za.ac.cput.service.IFaqService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;
import java.util.UUID;

/**
 * FaqController.java
 * This controller manages FAQ (Frequently Asked Questions) entries.
 * It provides public endpoints for retrieving FAQs and administrative endpoints for CRUD operations.
 *
 * @author Aqeel Hanslo (219374422)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/faqs")
@Tag(name = "FAQ Management", description = "Endpoints for viewing and managing Frequently Asked Questions (FAQs).")
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
     * Retrieves all non-deleted FAQs. This endpoint is intended for public access.
     *
     * @return A ResponseEntity containing a list of FAQ DTOs, or 204 No Content if none exist.
     */
    @Operation(summary = "Get all FAQs", description = "Retrieves a list of all publicly visible FAQs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved FAQs"),
            @ApiResponse(responseCode = "204", description = "No FAQs are available")
    })
    @GetMapping
    public ResponseEntity<List<FaqResponseDTO>> getAllFaqs() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all FAQs.", requesterId);

        List<Faq> allFaqs = faqService.getAll();
        if (allFaqs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FaqMapper.toDtoList(allFaqs));
    }

    /**
     * Retrieves a specific FAQ by its UUID. This endpoint is intended for public access.
     *
     * @param faqUuid The UUID of the FAQ to retrieve.
     * @return A ResponseEntity containing the FAQ DTO if found.
     */
    @Operation(summary = "Get FAQ by UUID", description = "Retrieves a specific FAQ by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ found", content = @Content(schema = @Schema(implementation = FaqResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "FAQ not found with the specified UUID")
    })
    @GetMapping("/{faqUuid}")
    public ResponseEntity<FaqResponseDTO> getFaqByUuid(
            @Parameter(description = "UUID of the FAQ to retrieve", required = true) @PathVariable UUID faqUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get FAQ by UUID: {}", requesterId, faqUuid);

        Faq faqEntity = faqService.read(faqUuid); // Service should throw ResourceNotFoundException
        return ResponseEntity.ok(FaqMapper.toDto(faqEntity));
    }

    /**
     * Creates a new FAQ. This operation should be restricted to administrators.
     *
     * @param faqCreateDTO The DTO containing data for the new FAQ.
     * @return A ResponseEntity containing the created FAQ DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Create a new FAQ (Admin)", description = "Creates a new FAQ. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "FAQ created successfully", content = @Content(schema = @Schema(implementation = FaqResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid FAQ data provided"),
            @ApiResponse(responseCode = "403", description = "User not authorized to create FAQs")
    })
    @PostMapping
    public ResponseEntity<FaqResponseDTO> createFaq(
            @Valid @RequestBody FaqCreateDTO faqCreateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to create a new FAQ with DTO: {}", requesterId, faqCreateDTO);

        Faq faqToCreate = FaqMapper.toEntity(faqCreateDTO);
        Faq createdFaqEntity = faqService.create(faqToCreate);

        log.info("Requester [{}]: Successfully created FAQ with UUID: {}", requesterId, createdFaqEntity.getUuid());
        return new ResponseEntity<>(FaqMapper.toDto(createdFaqEntity), HttpStatus.CREATED);
    }

    /**
     * Updates an existing FAQ identified by its UUID. This operation should be restricted to administrators.
     *
     * @param faqUuid      The UUID of the FAQ to update.
     * @param faqUpdateDTO The DTO containing the fields to update.
     * @return A ResponseEntity containing the updated FAQ DTO.
     */
    @Operation(summary = "Update an existing FAQ (Admin)", description = "Updates an existing FAQ by its UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ updated successfully", content = @Content(schema = @Schema(implementation = FaqResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update FAQs"),
            @ApiResponse(responseCode = "404", description = "FAQ not found with the specified UUID")
    })
    @PutMapping("/{faqUuid}")
    public ResponseEntity<FaqResponseDTO> updateFaq(
            @Parameter(description = "UUID of the FAQ to update", required = true) @PathVariable UUID faqUuid,
            @Valid @RequestBody FaqUpdateDTO faqUpdateDTO) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Attempting to update FAQ with UUID: {}", requesterId, faqUuid);

        Faq existingFaq = faqService.read(faqUuid);
        Faq faqWithUpdates = FaqMapper.applyUpdateDtoToEntity(faqUpdateDTO, existingFaq);
        Faq updatedFaq = faqService.update(faqWithUpdates);

        log.info("Requester [{}]: Successfully updated FAQ with UUID: {}", requesterId, updatedFaq.getUuid());
        return ResponseEntity.ok(FaqMapper.toDto(updatedFaq));
    }

    /**
     * Soft-deletes an FAQ by its UUID. This operation should be restricted to administrators.
     *
     * @param faqUuid The UUID of the FAQ to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete an FAQ by UUID (Admin)", description = "Soft-deletes an FAQ by its UUID. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "FAQ deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete FAQs"),
            @ApiResponse(responseCode = "404", description = "FAQ not found with the specified UUID")
    })
    @DeleteMapping("/{faqUuid}")
    public ResponseEntity<Void> deleteFaq(
            @Parameter(description = "UUID of the FAQ to delete", required = true) @PathVariable UUID faqUuid) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("ADMIN ACTION: Requester [{}] attempting to delete FAQ with UUID: {}", requesterId, faqUuid);

        Faq faqToDelete = faqService.read(faqUuid);
        faqService.delete(faqToDelete.getId());

        log.info("Requester [{}]: Successfully soft-deleted FAQ with UUID: {}.", requesterId, faqUuid);
        return ResponseEntity.noContent().build();
    }
}