package za.ac.cput.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.mapper.AboutUsMapper;
import za.ac.cput.exception.ResourceNotFoundException; // Good to use for consistency
import za.ac.cput.service.IAboutUsService; // Import the interface
import za.ac.cput.utils.SecurityUtils; // Import your new helper

import java.util.List;
import java.util.Optional;

/**
 * AboutUsController.java
 * This controller handles public requests related to "About Us" information.
 * It provides endpoints to retrieve specific, all, or the latest "About Us" content.
 * All endpoints are publicly accessible.
 *
 * Original Author (Version 1): Cwenga Dlova (214310671)
 * Original Date (Version 1): 24/09/2023
 *
 * Current Author (Version 2): Peter Buckingham (220165289)
 * Current Date (Version 2): 05 April 2023 // This seems like an older date, adjust if needed
 * Updated by: System/AI
 * Last Updated: 2025-05-28
 */
@RestController
@RequestMapping("/api/v1/about-us") // Assuming v1 is standard for public APIs too
public class AboutUsController {

    private static final Logger log = LoggerFactory.getLogger(AboutUsController.class);
    private final IAboutUsService aboutUsService; // Use the interface

    /**
     * Constructs an AboutUsController with the necessary AboutUs service.
     *
     * @param service The service implementation for "About Us" operations.
     */
    @Autowired
    public AboutUsController(IAboutUsService service) { // Inject IAboutUsService
        this.aboutUsService = service;
        log.info("AboutUsController initialized.");
    }

    /**
     * Retrieves a specific "About Us" entry by its internal integer ID.
     * This endpoint is publicly accessible.
     *
     * @param id The internal integer ID of the "About Us" entry to retrieve.
     * @return A ResponseEntity containing the {@link AboutUsResponseDTO} if found, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AboutUsResponseDTO> read(@PathVariable int id) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to read About Us content by ID: {}", requesterId, id);

        AboutUs readAbout = this.aboutUsService.read(id); // Assuming service.read(int) returns entity or null
        if (readAbout == null) {
            log.warn("Requester [{}]: About Us content not found for ID: {}", requesterId, id);
            // For consistency, you might want your service to throw ResourceNotFoundException
            // which can be handled by a global exception handler.
            // throw new ResourceNotFoundException("About Us content not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
        log.info("Requester [{}]: Successfully retrieved About Us content for ID: {}", requesterId, id);
        return ResponseEntity.ok(AboutUsMapper.toDto(readAbout));
    }

    /**
     * Retrieves all "About Us" entries.
     * This endpoint is publicly accessible.
     *
     * @return A ResponseEntity containing a list of {@link AboutUsResponseDTO}s, or 204 No Content if none exist.
     */
    @GetMapping
    public ResponseEntity<List<AboutUsResponseDTO>> getAll() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get all About Us entries.", requesterId);

        List<AboutUs> allAboutUs = aboutUsService.getAll();
        if (allAboutUs.isEmpty()) {
            log.info("Requester [{}]: No About Us entries found.", requesterId);
            return ResponseEntity.noContent().build(); // 204 No Content is appropriate for empty list
        }

        List<AboutUsResponseDTO> dtoList = AboutUsMapper.toDtoList(allAboutUs); // Efficient mapping
        log.info("Requester [{}]: Successfully retrieved {} About Us entries.", requesterId, dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves the latest "About Us" entry.
     * The "latest" is determined by being the last entry in the list retrieved by `getAll()`.
     * This endpoint is publicly accessible.
     *
     * @return A ResponseEntity containing the {@link AboutUsResponseDTO} of the latest entry, or 404 Not Found if no entries exist.
     */
    @GetMapping("/latest")
    public ResponseEntity<AboutUsResponseDTO> getLatest() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.info("Requester [{}]: Request to get the latest About Us entry.", requesterId);

        List<AboutUs> allAboutUs = aboutUsService.getAll();
        if (allAboutUs.isEmpty()) {
            log.warn("Requester [{}]: No About Us entries found, cannot get latest.", requesterId);
            return ResponseEntity.notFound().build(); // Or noContent() if that's preferred for "no latest"
        }

        // Get the last element from the list (assuming it's the newest)
        AboutUs latestAboutUsEntity = allAboutUs.get(allAboutUs.size() - 1);
        AboutUsResponseDTO latestDto = AboutUsMapper.toDto(latestAboutUsEntity);

        log.info("Requester [{}]: Successfully retrieved the latest About Us entry with ID: {} and UUID: {}",
                requesterId, latestAboutUsEntity.getId(), latestAboutUsEntity.getUuid());
        return ResponseEntity.ok(latestDto);
    }
}