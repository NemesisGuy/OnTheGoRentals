package za.ac.cput.controllers.security;

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
public class OAuth2Controller {

    private final GoogleOAuth2UserService googleOAuth2UserService;

    public OAuth2Controller(GoogleOAuth2UserService googleOAuth2UserService) {
        this.googleOAuth2UserService = googleOAuth2UserService;
    }

    // Endpoint to receive Google ID token from client (if client handles code exchange)
    @PostMapping("/google/login")
    public ResponseEntity<AuthResponseDto> loginWithGoogle(@RequestBody Map<String, String> payload) {
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