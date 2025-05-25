package za.ac.cput.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.entity.AboutUs; // Service works with this
import za.ac.cput.domain.dto.request.AboutUsCreateDTO;
import za.ac.cput.domain.dto.request.AboutUsUpdateDTO;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.mapper.AboutUsMapper;
import za.ac.cput.service.IAboutUsService; // Inject interface

import java.util.List;
import java.util.UUID;

/**
 * AdminAboutController.java
 * Admin Controller for About Us page content.
 * Author: Cwenga Dlova (214310671) // Updated by: [Your Name]
 * Date: 24/09/2023 // Updated: [Current Date]
 */

@RestController
@RequestMapping("/api/v1/admin/about-us") // Standardized base path
// @CrossOrigin(...) // Prefer global CORS
// @PreAuthorize("hasRole('ADMIN')") // Apply security at class level if all endpoints are admin
public class AdminAboutController { // Renamed class slightly for convention

    private final IAboutUsService aboutUsService;

    @Autowired
    public AdminAboutController(IAboutUsService aboutUsService) {
        this.aboutUsService = aboutUsService;
    }

    /**
     * Creates new "About Us" information.
     * Typically, there might only be one "About Us" entry, so this might be a PUT to a singleton resource
     * like PUT /api/v1/admin/about-us/content, or this POST assumes you can have multiple entries (less common).
     * For this example, we assume creation of a new record if one doesn't exist, or a distinct entry.
     */
    @PostMapping
    public ResponseEntity<AboutUsResponseDTO> createAboutUs(@Valid @RequestBody AboutUsCreateDTO createDto) {
        AboutUs aboutUsToCreate = AboutUsMapper.toEntity(createDto);
        AboutUs createdEntity = aboutUsService.create(aboutUsToCreate);
        return new ResponseEntity<>(AboutUsMapper.toDto(createdEntity), HttpStatus.CREATED);
    }

    /**
     * Retrieves "About Us" information by its UUID.
     */
    @GetMapping("/{aboutUsUuid}")
    public ResponseEntity<AboutUsResponseDTO> getAboutUsByUuid(@PathVariable UUID aboutUsUuid) {
        AboutUs aboutUsEntity = aboutUsService.read(aboutUsUuid);
        // Service should throw ResourceNotFoundException if not found
        return ResponseEntity.ok(AboutUsMapper.toDto(aboutUsEntity));
    }

    /**
     * Updates existing "About Us" information by its UUID.
     */
    @PutMapping("/{aboutUsUuid}")
    public ResponseEntity<AboutUsResponseDTO> updateAboutUs(
            @PathVariable UUID aboutUsUuid,
            @Valid @RequestBody AboutUsUpdateDTO updateDto
    ) {
        AboutUs existingAboutUs = aboutUsService.read(aboutUsUuid); // Fetches current state
        AboutUs aboutUsWithUpdates = AboutUsMapper.applyUpdateDtoToEntity(updateDto, existingAboutUs); // Creates new state
        AboutUs persistedAboutUs = aboutUsService.update(aboutUsWithUpdates); // Service saves new state (JPA updates)
        return ResponseEntity.ok(AboutUsMapper.toDto(persistedAboutUs));
    }

    /**
     * Retrieves all "About Us" entries (e.g., if you allow multiple or historical versions).
     * For admin, this might include soft-deleted entries.
     */
    @GetMapping
    public ResponseEntity<List<AboutUsResponseDTO>> getAllAboutUsEntries() {
        List<AboutUs> aboutUsList = aboutUsService.getAll(); // Or getAllNonDeleted()
        List<AboutUsResponseDTO> dtoList = AboutUsMapper.toDtoList(aboutUsList);
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Soft-deletes "About Us" information by its UUID.
     */
    @DeleteMapping("/{aboutUsUuid}")
    public ResponseEntity<Void> deleteAboutUs(@PathVariable UUID aboutUsUuid) {
        AboutUs aboutUs = aboutUsService.read(aboutUsUuid);
        boolean deleted = aboutUsService.delete(aboutUs.getId());
        // Service's softDeleteByUuid should throw ResourceNotFoundException if not found,
        // or controller checks boolean.
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    // ----- Original Integer ID based methods (for reference or if still needed internally by admin) -----
    // These should ideally be phased out from public-facing admin APIs in favor of UUIDs.

    /*
    @GetMapping("/read/int/{id}") // Distinguish path if keeping int ID access
    public ResponseEntity<AboutUsResponseDTO> readByIntId(@PathVariable("id") int id) {
        AboutUs readAbout = this.aboutUsService.read(id);
        if (readAbout == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(AboutUsMapper.toDto(readAbout));
    }

    @DeleteMapping("/delete/int/{id}") // Distinguish path
    public ResponseEntity<?> deleteByIntId(@PathVariable("id") int id) {
        boolean deleted = this.aboutUsService.delete(id); // Assumes this is soft delete
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("AboutUs with ID " + id + " soft-deleted."); // Or noContent()
    }
    */
}