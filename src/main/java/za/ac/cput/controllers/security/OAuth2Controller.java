package za.ac.cput.controllers.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.service.impl.GoogleOAuth2UserService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * OAuth2Controller.java
 * Controller for handling third-party authentication using OAuth2 providers like Google.
 * This controller provides an endpoint to process an ID token sent from the client.
 *
 * @author Peter Buckingham (220165289)
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/oauth2") // Standardized path
@CrossOrigin(origins = "*") // Note: For production, restrict this to your frontend's domain.
@Tag(name = "OAuth2 Authentication", description = "Endpoints for third-party authentication using OAuth2 providers like Google.")
public class OAuth2Controller {

    private final GoogleOAuth2UserService googleOAuth2UserService;

    public OAuth2Controller(GoogleOAuth2UserService googleOAuth2UserService) {
        this.googleOAuth2UserService = googleOAuth2UserService;
    }

    /**
     * Authenticates a user with a Google ID token provided by the client.
     * Upon successful validation of the Google token, this endpoint will either find an existing user
     * or create a new one, and then return the application's own JWT access token for the user's session.
     *
     * @param payload A JSON object containing the Google ID token, e.g., { "idToken": "..." }.
     * @return A ResponseEntity containing an {@link AuthResponseDto} with the application's JWT access token.
     */
    @Operation(
            summary = "Login with Google",
            description = "Authenticates a user by validating a Google ID token sent from the client. Returns an application-specific JWT upon success."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, JWT returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - The provided ID token is invalid, malformed, or missing"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Could not process the token due to a server-side issue")
    })
    @PostMapping("/google/login")
    public ResponseEntity<AuthResponseDto> loginWithGoogle(
            @Parameter(description = "A JSON payload containing the Google ID token. e.g., {\"idToken\": \"your_token_here\"}", required = true)
            @RequestBody Map<String, String> payload) {

        String idTokenString = payload.get("idToken");
        if (idTokenString == null || idTokenString.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            AuthResponseDto authResponseDto = googleOAuth2UserService.processGoogleIdToken(idTokenString);
            return ResponseEntity.ok(authResponseDto);
        } catch (GeneralSecurityException | IOException e) {
            // Log the exception for debugging server-side issues (e.g., cannot reach Google's certs)
            // log.error("Error processing Google ID token: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        } catch (IllegalArgumentException e) {
            // This is often thrown for an invalid token format or signature.
            // log.warn("Invalid Google ID token received: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}