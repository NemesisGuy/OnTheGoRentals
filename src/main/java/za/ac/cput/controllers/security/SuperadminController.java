package za.ac.cput.controllers.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SuperadminController.java
 * Controller for superadmin-specific test or utility endpoints.
 * These endpoints are protected to be accessible only by users with superadmin privileges.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/superadmin")
@RequiredArgsConstructor
@Tag(name = "Superadmin Utilities", description = "Endpoints accessible only to superadministrators for utility or test purposes.")
@SecurityRequirement(name = "bearerAuth")
public class SuperadminController {

    private static final Logger log = LoggerFactory.getLogger(SuperadminController.class);

    /**
     * A simple test endpoint to verify that superadmin-level access is correctly configured and working.
     * When a request is successfully authenticated and authorized, it returns a simple greeting.
     *
     * @return A "Hi" string as a plain text response.
     */
    @Operation(
            summary = "Superadmin Greeting",
            description = "A simple endpoint that returns a greeting. Used to verify that the requester has superadmin access."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, user is a superadmin",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Hi"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authenticated user is not a superadmin", content = @Content)
    })
    @GetMapping("/hi")
    public String sayHi() {
        log.info("Superadmin 'sayHi' endpoint accessed successfully.");
        return "Hi";
    }
}