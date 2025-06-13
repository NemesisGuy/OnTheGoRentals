package za.ac.cput.controllers.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * This controller might be used for simple authenticated 'hello' checks for admin roles.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date, if known, otherwise current date]
 */
@RestController
@RequestMapping("/api/v1/admins") // Standardized path
@RequiredArgsConstructor
@Tag(name = "Admin Security Utilities", description = "Endpoints for admin-specific utility or test operations related to security.")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    /**
     * A simple test endpoint to check if admin-level access is working.
     * Resource Endpoint: http://localhost:8080/api/v1/admins/hello (Example with v1)
     *
     * @return A "Hello" string.
     */
    @GetMapping("/hello")
    @Operation(summary = "Admin Hello", description = "A simple test endpoint to verify admin role access.")
    @ApiResponse(responseCode = "200", description = "Successfully returns hello message")
    public String sayHello() {
        log.info("Admin sayHello endpoint accessed.");
        return "Hello";
    }
}
