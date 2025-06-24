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
import za.ac.cput.domain.dto.request.FaqCreateDTO;
import za.ac.cput.domain.dto.request.FaqUpdateDTO;
import za.ac.cput.domain.dto.response.FaqResponseDTO;
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.domain.mapper.FaqMapper;
import za.ac.cput.service.IFaqService;

import java.util.List;
import java.util.UUID;

/**
 * AdminFaqController.java
 * Controller for administrators to manage FAQ (Frequently Asked Questions) entries.
 * Allows admins to perform full CRUD operations on FAQs.
 *
 * @author Aqeel Hanslo (219374422)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/faqs")
@Tag(name = "Admin: FAQ Management", description = "Endpoints for administrators to manage FAQ entries.")
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
     * Creates a new FAQ.
     *
     * @param faqCreateDTO The DTO containing the data for the new FAQ.
     * @return A ResponseEntity containing the created FAQ DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Create a new FAQ", description = "Allows an administrator to add a new FAQ to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "FAQ created successfully", content = @Content(schema = @Schema(implementation = FaqResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid FAQ data provided")
    })
    @PostMapping
    public ResponseEntity<FaqResponseDTO> createFaq(@Valid @RequestBody FaqCreateDTO faqCreateDTO) {
        log.info("Admin request to create a new FAQ with DTO: {}", faqCreateDTO);
        Faq faqToCreate = FaqMapper.toEntity(faqCreateDTO);
        Faq createdFaqEntity = faqService.create(faqToCreate);
        log.info("Successfully created FAQ with UUID: {}", createdFaqEntity.getUuid());
        return new ResponseEntity<>(FaqMapper.toDto(createdFaqEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves all FAQ entries for administrative view.
     *
     * @return A ResponseEntity containing a list of all FAQ DTOs.
     */
    @Operation(summary = "Get all FAQs (Admin)", description = "Retrieves a list of all FAQs for administrative view.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of FAQs"),
            @ApiResponse(responseCode = "204", description = "No FAQs found")
    })
    @GetMapping
    public ResponseEntity<List<FaqResponseDTO>> getAllFaqsForAdmin() {
        log.info("Admin request to get all FAQs.");
        List<Faq> faqs = faqService.getAll();
        if (faqs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FaqMapper.toDtoList(faqs));
    }

    /**
     * Retrieves a specific FAQ by its UUID.
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
        log.info("Admin request to get FAQ by UUID: {}", faqUuid);
        Faq faqEntity = faqService.read(faqUuid);
        return ResponseEntity.ok(FaqMapper.toDto(faqEntity));
    }

    /**
     * Updates an existing FAQ identified by its UUID.
     *
     * @param faqUuid      The UUID of the FAQ to update.
     * @param faqUpdateDTO The DTO containing the updated data for the FAQ.
     * @return A ResponseEntity containing the updated FAQ DTO.
     */
    @Operation(summary = "Update an existing FAQ", description = "Updates the details of an existing FAQ by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ updated successfully", content = @Content(schema = @Schema(implementation = FaqResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "404", description = "FAQ not found with the specified UUID")
    })
    @PutMapping("/{faqUuid}")
    public ResponseEntity<FaqResponseDTO> updateFaq(
            @Parameter(description = "UUID of the FAQ to update", required = true) @PathVariable UUID faqUuid,
            @Valid @RequestBody FaqUpdateDTO faqUpdateDTO) {
        log.info("Admin request to update FAQ with UUID: {}", faqUuid);
        Faq existingFaq = faqService.read(faqUuid);
        Faq faqWithUpdates = FaqMapper.applyUpdateDtoToEntity(faqUpdateDTO, existingFaq);
        Faq persistedUpdatedFaq = faqService.update(faqWithUpdates);
        log.info("Successfully updated FAQ with UUID: {}", persistedUpdatedFaq.getUuid());
        return ResponseEntity.ok(FaqMapper.toDto(persistedUpdatedFaq));
    }

    /**
     * Soft-deletes an FAQ by its UUID.
     *
     * @param faqUuid The UUID of the FAQ to delete.
     * @return A ResponseEntity with status 204 No Content if successful.
     */
    @Operation(summary = "Delete an FAQ", description = "Soft-deletes an FAQ by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "FAQ deleted successfully"),
            @ApiResponse(responseCode = "404", description = "FAQ not found with the specified UUID")
    })
    @DeleteMapping("/{faqUuid}")
    public ResponseEntity<Void> deleteFaq(
            @Parameter(description = "UUID of the FAQ to delete", required = true) @PathVariable UUID faqUuid) {
        log.warn("ADMIN ACTION: Request to delete FAQ with UUID: {}", faqUuid);
        Faq faqToDelete = faqService.read(faqUuid);
        faqService.delete(faqToDelete.getId());
        log.info("Successfully soft-deleted FAQ with UUID: {}.", faqUuid);
        return ResponseEntity.noContent().build();
    }
}