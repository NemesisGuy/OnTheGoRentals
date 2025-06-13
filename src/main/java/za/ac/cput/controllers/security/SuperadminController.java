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
 * These endpoints are typically protected to be accessible only by users with superadmin privileges.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date, if known, otherwise current date]
 */
@RestController
@RequestMapping("/api/v1/superadmin") // Standardized path
@RequiredArgsConstructor
@Tag(name = "Superadmin Utilities", description = "Endpoints accessible only to superadministrators for utility or test purposes.")
@SecurityRequirement(name = "bearerAuth")
public class SuperadminController {

    private static final Logger log = LoggerFactory.getLogger(SuperadminController.class);

    /**
     * A simple test endpoint to check if superadmin-level access is working.
     * Resource Endpoint: http://localhost:8080/api/v1/superadmin/hi (Example with v1)
     *
     * @return A "Hi" string.
     */
    //RessourceEndPoint:http://localhost:8087/api/superadmin/hi // This will be updated by the RequestMapping
    @Operation(
            summary = "Superadmin Greeting",
            description = "A simple endpoint that returns a greeting. Used to verify superadmin access."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized as superadmin", content = @Content)
    })
    @GetMapping("/hi")
    public String sayHi() {
        log.info("Superadmin sayHi endpoint accessed.");
        return "Hi";
    }
}
