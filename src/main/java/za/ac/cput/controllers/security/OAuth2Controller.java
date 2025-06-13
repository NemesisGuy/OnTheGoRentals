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

@RestController
@RequestMapping("/api/oauth2")
@CrossOrigin(origins = "*")
@Tag(name = "OAuth2 Authentication", description = "Endpoints for third-party authentication using OAuth2 providers like Google.")
public class OAuth2Controller {

    private final GoogleOAuth2UserService googleOAuth2UserService;

    public OAuth2Controller(GoogleOAuth2UserService googleOAuth2UserService) {
        this.googleOAuth2UserService = googleOAuth2UserService;
    }

    // Endpoint to receive Google ID token from client (if client handles code exchange)
    @Operation(
            summary = "Login with Google",
            description = "Authenticates a user with a Google ID token and returns a JWT access token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid or missing ID token", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error processing the token", content = @Content)
    })
    @PostMapping("/google/login")
    public ResponseEntity<AuthResponseDto> loginWithGoogle(
            @Parameter(description = "Payload containing the Google ID token", required = true)
            @RequestBody Map<String, String> payload) {
        String idTokenString = payload.get("idToken");
        if (idTokenString == null || idTokenString.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Or an error DTO
        }
        try {
            AuthResponseDto authResponseDto = googleOAuth2UserService.processGoogleIdToken(idTokenString);
            return ResponseEntity.ok(authResponseDto);
        } catch (GeneralSecurityException | IOException e) {
            // Log error
            return ResponseEntity.status(500).body(null); // Or an error DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Invalid token
        }
    }

    // Alternative: Endpoint to receive authorization code from client (server exchanges code)
    // This requires more setup with OAuth2RestTemplate or WebClient for code exchange.
    // The idToken flow is simpler if the client library can provide the idToken.
    /*
    @PostMapping("/google/code")
    public ResponseEntity<AuthResponseDto> handleGoogleAuthCode(@RequestBody Map<String, String> payload) {
        String authCode = payload.get("authCode");
        String redirectUri = payload.get("redirectUri"); // Client must send the redirect_uri used
        // ... logic to exchange authCode for tokens with Google ...
        // ... then call processGoogleIdToken with the id_token received from Google ...
    }
    */
}
