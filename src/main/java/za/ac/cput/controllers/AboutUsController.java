package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.domain.mapper.AboutUsMapper;
import za.ac.cput.service.IAboutUsService;
import za.ac.cput.utils.SecurityUtils;

import java.util.List;

/**
 * AboutUsController.java
 * This controller handles public requests related to "About Us" information.
 * It provides endpoints to retrieve specific, all, or the latest "About Us" content.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/about-us")
@Tag(name = "About Us", description = "Endpoints for retrieving public 'About Us' company information.")
public class AboutUsController {

    private static final Logger log = LoggerFactory.getLogger(AboutUsController.class);
    private final IAboutUsService aboutUsService;

    /**
     * Constructs an AboutUsController with the necessary AboutUs service.
     *
     * @param service The service implementation for "About Us" operations.
     */
    @Autowired
    public AboutUsController(IAboutUsService service) {
        this.aboutUsService = service;
        log.info("AboutUsController initialized.");
    }

    /**
     * Retrieves all "About Us" entries. This endpoint is publicly accessible.
     *
     * @return A ResponseEntity containing a list of DTOs, or 204 No Content if none exist.
     */
    @Operation(summary = "Get all 'About Us' entries", description = "Retrieves all publicly available 'About Us' entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved 'About Us' entries"),
            @ApiResponse(responseCode = "204", description = "No 'About Us' entries exist")
    })
    @GetMapping
    public ResponseEntity<List<AboutUsResponseDTO>> getAll() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all About Us entries.", requesterId);

        List<AboutUs> allAboutUs = aboutUsService.getAll();
        if (allAboutUs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(AboutUsMapper.toDtoList(allAboutUs));
    }

    /**
     * Retrieves the latest "About Us" entry, which is considered the currently active one.
     * This endpoint is publicly accessible.
     *
     * @return A ResponseEntity containing the DTO of the latest entry, or 404 Not Found if none exist.
     */
    @Operation(summary = "Get latest 'About Us' entry", description = "Retrieves the most recent 'About Us' entry, which is considered the current version.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest entry found", content = @Content(schema = @Schema(implementation = AboutUsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No 'About Us' entries exist in the system")
    })
    @GetMapping("/latest")
    public ResponseEntity<AboutUsResponseDTO> getLatest() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get the latest About Us entry.", requesterId);

        List<AboutUs> allAboutUs = aboutUsService.getAll();
        if (allAboutUs.isEmpty()) {
            log.warn("Requester [{}]: No About Us entries found, cannot get latest.", requesterId);
            return ResponseEntity.notFound().build();
        }
        AboutUs latestAboutUsEntity = allAboutUs.get(allAboutUs.size() - 1);
        return ResponseEntity.ok(AboutUsMapper.toDto(latestAboutUsEntity));
    }

    /**
     * Retrieves a specific "About Us" entry by its internal integer ID.
     * Note: Exposing internal integer IDs is generally discouraged. Prefer using UUIDs for public-facing endpoints.
     * This endpoint is kept for legacy purposes or specific use cases.
     *
     * @param id The internal integer ID of the "About Us" entry to retrieve.
     * @return A ResponseEntity containing the DTO if found, or 404 Not Found.
     */
    @Operation(summary = "Get 'About Us' by ID", description = "Retrieves a specific 'About Us' entry by its internal integer ID. Using the '/latest' endpoint is generally preferred for public consumption.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entry found", content = @Content(schema = @Schema(implementation = AboutUsResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Entry not found with the specified ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AboutUsResponseDTO> read(
            @Parameter(description = "Internal integer ID of the 'About Us' entry to retrieve.", required = true) @PathVariable int id) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to read About Us content by ID: {}", requesterId, id);

        AboutUs readAbout = this.aboutUsService.read(id);
        if (readAbout == null) {
            log.warn("Requester [{}]: About Us content not found for ID: {}", requesterId, id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AboutUsMapper.toDto(readAbout));
    }
}