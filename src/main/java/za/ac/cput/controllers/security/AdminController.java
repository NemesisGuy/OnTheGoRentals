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
 * AdminController.java
 * Controller for admin-specific test or utility endpoints.
 * This controller is used for simple authenticated checks for admin roles.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admin Utilities", description = "Endpoints for admin-specific utility or test operations related to security.")
@SecurityRequirement(name = "bearerAuth") // Indicates all endpoints here require authentication
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    /**
     * A simple test endpoint to check if admin-level access is correctly configured and working.
     * When a request is successfully authenticated and authorized with an admin role, it returns a simple greeting.
     *
     * @return A "Hello" string as a plain text response.
     */
    @Operation(
            summary = "Admin Greeting",
            description = "A simple endpoint that returns a greeting. Used to verify that the requester has an admin role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, user has an admin role",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Hello"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Authenticated user does not have an admin role", content = @Content)
    })
    @GetMapping("/hello")
    public String sayHello() {
        log.info("Admin 'sayHello' endpoint accessed successfully.");
        return "Hello";
    }
}