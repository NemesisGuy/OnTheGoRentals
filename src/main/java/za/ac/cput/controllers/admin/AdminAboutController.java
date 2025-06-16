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
import za.ac.cput.domain.dto.request.AboutUsCreateDTO;
import za.ac.cput.domain.dto.request.AboutUsUpdateDTO;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.domain.mapper.AboutUsMapper;
import za.ac.cput.service.IAboutUsService;

import java.util.List;
import java.util.UUID;

/**
 * AdminAboutController.java
 * Admin Controller for managing "About Us" page content.
 * Allows administrators to perform CRUD operations on "About Us" entries.
 *
 * @author Cwenga Dlova (214310671)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admin/about-us")
@Tag(name = "Admin: About Us Management", description = "Endpoints for administrators to manage 'About Us' page content.")
public class AdminAboutController {

    private static final Logger log = LoggerFactory.getLogger(AdminAboutController.class);
    private final IAboutUsService aboutUsService;

    /**
     * Constructs an AdminAboutController with the necessary AboutUs service.
     *
     * @param aboutUsService The service implementation for "About Us" operations.
     */
    @Autowired
    public AdminAboutController(IAboutUsService aboutUsService) {
        this.aboutUsService = aboutUsService;
        log.info("AdminAboutController initialized.");
    }

    /**
     * Creates a new "About Us" information entry.
     *
     * @param createDto The DTO containing the data for the new entry.
     * @return A ResponseEntity containing the created DTO and HTTP status 201 Created.
     */
    @Operation(summary = "Create new 'About Us' content", description = "Allows an administrator to create a new 'About Us' information entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "'About Us' entry created successfully", content = @Content(schema = @Schema(implementation = AboutUsResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User does not have admin privileges")
    })
    @PostMapping
    public ResponseEntity<AboutUsResponseDTO> createAboutUs(@Valid @RequestBody AboutUsCreateDTO createDto) {
        log.info("Admin request to create new About Us content with DTO: {}", createDto);
        AboutUs aboutUsToCreate = AboutUsMapper.toEntity(createDto);
        AboutUs createdEntity = aboutUsService.create(aboutUsToCreate);
        log.info("Successfully created About Us entry with UUID: {}", createdEntity.getUuid());
        return new ResponseEntity<>(AboutUsMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves all "About Us" entries.
     *
     * @return A ResponseEntity containing a list of all "About Us" DTOs.
     */
    @Operation(summary = "Get all 'About Us' entries", description = "Retrieves all 'About Us' entries, including historical or soft-deleted ones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "204", description = "No 'About Us' entries found")
    })
    @GetMapping
    public ResponseEntity<List<AboutUsResponseDTO>> getAllAboutUsEntries() {
        log.info("Admin request to get all About Us entries.");
        List<AboutUs> aboutUsList = aboutUsService.getAll();
        if (aboutUsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(AboutUsMapper.toDtoList(aboutUsList));
    }

    /**
     * Retrieves a specific "About Us" entry by its UUID.
     *
     * @param aboutUsUuid The UUID of the "About Us" entry to retrieve.
     * @return A ResponseEntity containing the DTO if found.
     */
    @Operation(summary = "Get 'About Us' content by UUID", description = "Retrieves a specific 'About Us' entry by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entry found", content = @Content(schema = @Schema(implementation = AboutUsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Entry not found with the specified UUID")
    })
    @GetMapping("/{aboutUsUuid}")
    public ResponseEntity<AboutUsResponseDTO> getAboutUsByUuid(
            @Parameter(description = "UUID of the 'About Us' entry to retrieve", required = true) @PathVariable UUID aboutUsUuid) {
        log.info("Admin request to get About Us content by UUID: {}", aboutUsUuid);
        AboutUs aboutUsEntity = aboutUsService.read(aboutUsUuid);
        return ResponseEntity.ok(AboutUsMapper.toDto(aboutUsEntity));
    }

    /**
     * Updates an existing "About Us" entry identified by its UUID.
     *
     * @param aboutUsUuid The UUID of the "About Us" entry to update.
     * @param updateDto   The DTO containing the updated data.
     * @return A ResponseEntity containing the updated DTO.
     */
    @Operation(summary = "Update 'About Us' content", description = "Updates an existing 'About Us' entry by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entry updated successfully", content = @Content(schema = @Schema(implementation = AboutUsResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data provided"),
            @ApiResponse(responseCode = "404", description = "Entry not found with the specified UUID")
    })
    @PutMapping("/{aboutUsUuid}")
    public ResponseEntity<AboutUsResponseDTO> updateAboutUs(
            @Parameter(description = "UUID of the 'About Us' entry to update", required = true) @PathVariable UUID aboutUsUuid,
            @Valid @RequestBody AboutUsUpdateDTO updateDto) {
        log.info("Admin request to update About Us content with UUID: {}", aboutUsUuid);
        AboutUs existingAboutUs = aboutUsService.read(aboutUsUuid);
        AboutUs aboutUsWithUpdates = AboutUsMapper.applyUpdateDtoToEntity(updateDto, existingAboutUs);
        AboutUs persistedAboutUs = aboutUsService.update(aboutUsWithUpdates);
        log.info("Successfully updated About Us entry with UUID: {}", persistedAboutUs.getUuid());
        return ResponseEntity.ok(AboutUsMapper.toDto(persistedAboutUs));
    }

    /**
     * Soft-deletes an "About Us" entry identified by its UUID.
     *
     * @param aboutUsUuid The UUID of the "About Us" entry to delete.
     * @return A ResponseEntity with status 204 No Content.
     */
    @Operation(summary = "Delete 'About Us' content", description = "Soft-deletes an 'About Us' entry by its UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Entry not found with the specified UUID")
    })
    @DeleteMapping("/{aboutUsUuid}")
    public ResponseEntity<Void> deleteAboutUs(
            @Parameter(description = "UUID of the 'About Us' entry to delete", required = true) @PathVariable UUID aboutUsUuid) {
        log.warn("ADMIN ACTION: Request to delete About Us content with UUID: {}", aboutUsUuid);
        AboutUs aboutUsToDelete = aboutUsService.read(aboutUsUuid);
        aboutUsService.delete(aboutUsToDelete.getId());
        log.info("Successfully soft-deleted About Us entry with UUID: {}", aboutUsUuid);
        return ResponseEntity.noContent().build();
    }
}