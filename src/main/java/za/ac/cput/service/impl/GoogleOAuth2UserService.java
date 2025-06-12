package za.ac.cput.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.entity.security.RefreshToken;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.AuthProvider;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IRefreshTokenService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoogleOAuth2UserService {

    private final UserRepository UserRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // For generating a dummy password for OAuth users
    private final JwtUtilities jwtUtilities;
    private final IRefreshTokenService refreshTokenService;
    private final GoogleIdTokenVerifier verifier;

    @Value("${jwt.expiration}")
    private Long accessTokenExpirationMs;

    // The google.oauth2.audience should be your Google Client ID
    public GoogleOAuth2UserService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String googleClientId,
                                   UserRepository UserRepository, IRoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder, JwtUtilities jwtUtilities,
                                   IRefreshTokenService refreshTokenService) {
        this.UserRepository = UserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtilities = jwtUtilities;
        this.refreshTokenService = refreshTokenService;

        // Initialize Google ID Token Verifier
        // You might need to add google-http-client-jackson2 dependency if not already present
        NetHttpTransport transport = new NetHttpTransport();
        GsonFactory jsonFactory = new GsonFactory(); // Or JacksonFactory
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Transactional
    public AuthResponseDto processGoogleIdToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleUserId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            // String familyName = (String) payload.get("family_name");
            // String givenName = (String) payload.get("given_name");

            if (!emailVerified) {
                throw new IllegalArgumentException("Google email not verified.");
            }

            Optional<User> userOptional = UserRepository.findByEmail(email); // Prioritize email
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
                // User exists. Update if necessary (e.g., if they signed up locally first)
                if (user.getAuthProvider() == AuthProvider.LOCAL) {
                    // This is an existing local user. Link Google account.
                    user.setAuthProvider(AuthProvider.GOOGLE);
                    user.setGoogleId(googleUserId);
                    // Optionally update name/picture if local versions are empty or different
                    if (user.getFirstName() == null || user.getFirstName().isEmpty())
                        user.setFirstName(extractFirstName(name));
                    if (user.getLastName() == null || user.getLastName().isEmpty())
                        user.setLastName(extractLastName(name, extractFirstName(name)));
                 /*   if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty())
                        user.setProfileImageUrl(pictureUrl);*/
                    UserRepository.save(user);
                } else if (user.getAuthProvider() == AuthProvider.GOOGLE && (user.getGoogleId() == null || !user.getGoogleId().equals(googleUserId))) {
                    // Email exists, registered with Google, but Google ID mismatch (highly unlikely if email is verified unique)
                    // Handle this edge case, maybe log an error or throw exception
                    throw new RuntimeException("User with this email exists but Google ID mismatch.");
                }
                // If already a GOOGLE user with matching googleId, no major changes needed to user entity.
            } else {
                // New user
                user = new User();
                user.setEmail(email);
                user.setFirstName(extractFirstName(name));
                user.setLastName(extractLastName(name, extractFirstName(name)));
                /*    user.setProfileImageUrl(pictureUrl);*/
                user.setAuthProvider(AuthProvider.GOOGLE);
                user.setGoogleId(googleUserId);
                // OAuth2 users usually don't have a password managed by our system
                // Set a secure random password or leave it null if your UserDetails handles it.
                // Setting a random one avoids issues if other parts of system expect a non-null password.
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                Role userRole = roleRepository.findByRoleName(RoleName.USER);
                user.setRoles(Collections.singletonList(userRole));
                UserRepository.save(user);
            }

            // Generate your application's tokens
            List<String> rolesNames = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
            String appAccessToken = jwtUtilities.generateToken(user, rolesNames);
            RefreshToken appRefreshToken = refreshTokenService.createRefreshToken(user.getId());

            return new AuthResponseDto(
                    appAccessToken,
                    //    appRefreshToken.getToken(),
                    "Bearer",
                    accessTokenExpirationMs,
                    user.getEmail(),
                    rolesNames
            );

        } else {
            throw new IllegalArgumentException("Invalid Google ID token.");
        }
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.split("\\s+");
        return parts.length > 0 ? parts[0] : fullName;
    }

    private String extractLastName(String fullName, String firstName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        if (firstName != null && !firstName.isEmpty() && fullName.startsWith(firstName)) {
            String lastNamePart = fullName.substring(firstName.length()).trim();
            return !lastNamePart.isEmpty() ? lastNamePart : ""; // If only first name provided, last name might be empty
        }
        String[] parts = fullName.split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : ""; // Fallback if first name extraction wasn't perfect
    }
}