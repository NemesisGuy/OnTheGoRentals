package za.ac.cput.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import za.ac.cput.domain.entity.security.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * JwtUtilities.java
 * Utility class for JSON Web Token (JWT) operations, including generation,
 * validation, and extraction of claims. This class is central to handling
 * stateless authentication with JWTs.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Component // @Slf4j from Lombok can be used instead of manual logger if preferred
public class JwtUtilities {

    private static final Logger log = LoggerFactory.getLogger(JwtUtilities.class); // Manual SLF4J Logger

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs; // Renamed for clarity (milliseconds)

    // --- Values for Cookie Generation (if this class handles it) ---
    // These are needed if you move cookie generation from UserServiceImpl to here.
    @Value("${app.security.access-cookie.name:access_token}") // Example: default if not set
    private String accessTokenCookieName;

    @Value("${app.security.refresh-cookie.name:refresh_token}") // Used by UserServiceImpl
    private String refreshTokenCookieName;

    @Value("${app.security.cookie.path:/}") // Default path
    private String cookiePath;

    @Value("${app.security.cookie.secure:true}") // Default to true
    private boolean secureCookie;

    // @Value("${jwt.refresh-token.expiration-ms}") // This is used by RefreshTokenService/UserService for DB entry
    // private Long refreshTokenDurationMs;         // This utility would use it if generating refresh token cookie here.

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username (subject) from the JWT token.
     * For this application, the email is often used as the username in the token subject.
     *
     * @param token The JWT token string.
     * @return The username (subject claim).
     */
    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        log.trace("Extracted username/subject '{}' from token.", username);
        return username;
    }

    /**
     * Extracts the email from the "email" claim within the JWT token.
     *
     * @param token The JWT token string.
     * @return The email address from the token's "email" claim.
     */
    public String extractUserEmail(String token) {
        String email = extractClaim(token, claims -> claims.get("email", String.class));
        log.trace("Extracted email '{}' from token.", email);
        return email;
    }

    /**
     * Extracts the user's UUID from the "uuid" claim within the JWT token.
     *
     * @param token The JWT token string.
     * @return The user's UUID string from the token's "uuid" claim.
     */
    public String extractUserUuid(String token) {
        String uuid = extractClaim(token, claims -> claims.get("uuid", String.class));
        log.trace("Extracted UUID '{}' from token.", uuid);
        return uuid;
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token string.
     * @return The Claims object containing all data from the token payload.
     * @throws JwtException if the token is invalid or cannot be parsed.
     */
    public Claims extractAllClaims(String token) {
        log.trace("Attempting to extract all claims from token.");
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token) // Changed from parseClaimsJws to parseSignedClaims for modern API
                .getPayload();
    }

    /**
     * Generic method to extract a specific claim from the JWT token using a claims resolver function.
     *
     * @param token          The JWT token string.
     * @param claimsResolver A function that takes Claims and returns the desired claim value.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token string.
     * @return The expiration date of the token.
     */
    public Date extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.trace("Extracted expiration date '{}' from token.", expiration);
        return expiration;
    }

    /**
     * Validates a JWT token against UserDetails.
     * Checks if the username/email in the token matches the UserDetails' username
     * and if the token is not expired.
     *
     * @param token       The JWT token string.
     * @param userDetails The {@link UserDetails} of the user to validate against.
     * @return {@code true} if the token is valid for the user, {@code false} otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String emailFromToken = extractUserEmail(token);
        boolean isValid = emailFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
        log.debug("Validating token for user '{}'. Token valid: {}", userDetails.getUsername(), isValid);
        return isValid;
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token The JWT token string.
     * @return {@code true} if the token is expired, {@code false} otherwise.
     */
    public Boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.trace("Checking token expiration. Is expired: {}", expired);
        return expired;
    }

    /**
     * Generates a JWT access token for a given user with specified roles.
     * Includes user's UUID, email, and roles as claims.
     *
     * @param user  The {@link User} entity for whom the token is generated.
     * @param roles A list of role names (Strings) to include in the token.
     * @return The generated JWT access token string.
     */
    public String generateToken(User user, List<String> roles) {
        log.info("Generating JWT access token for user: {}", user.getEmail());
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(user.getEmail()) // Standard subject, often the username/email
                .claim("uuid", user.getUuid().toString()) // Store UUID as string
                .claim("email", user.getEmail())
                .claim("roles", roles) // Changed from "role" to "roles" for list
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtExpirationMs, ChronoUnit.MILLIS)))
                .signWith(getSigningKey(), Jwts.SIG.HS512) // Specify algorithm explicitly
                .compact();
        log.debug("Generated access token (partial for logging): {}...", token.substring(0, Math.min(20, token.length())));
        return token;
    }

    /**
     * Validates the signature and expiration of a JWT token string.
     *
     * @param token The JWT token string to validate.
     * @return {@code true} if the token is structurally valid, signed correctly, and not expired; {@code false} otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            log.trace("Token validation successful.");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty or invalid: {}", e.getMessage());
        } catch (JwtException e) { // Catch-all for other JWT issues
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        log.debug("Token validation failed.");
        return false;
    }

    /**
     * Extracts the JWT token from the Authorization header of an HttpServletRequest.
     * Expects the "Bearer " prefix.
     *
     * @param httpServletRequest The incoming HTTP request.
     * @return The JWT token string if present and correctly formatted, otherwise {@code null}.
     */
    public String getToken(HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION); // Use HttpHeaders constant
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.trace("Extracted 'Bearer' token from Authorization header.");
            return token;
        }
        log.trace("No 'Bearer' token found in Authorization header.");
        return null;
    }

    // --- Cookie Generation Methods (can be here or in AuthService/UserService) ---
    // If UserServiceImpl calls these, they should be public.
    // These are examples; UserServiceImpl already has its own cookie generation.
    // Keeping them here as a reference for a centralized utility.

    /**
     * Generates an HTTP-only, secure cookie for the access token.
     * Note: Storing access tokens in cookies has security implications (CSRF if not SameSite=Strict).
     * It's often preferred to send access tokens in the response body or Authorization header.
     *
     * @param token The access token string.
     * @return A {@link ResponseCookie} object for the access token.
     */
    public ResponseCookie generateAccessTokenCookie(String token) {
        log.debug("Generating access token cookie. Name: '{}'", accessTokenCookieName);
        return ResponseCookie.from(accessTokenCookieName, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path(cookiePath)
                .maxAge(TimeUnit.MILLISECONDS.toSeconds(jwtExpirationMs))
                .sameSite("Lax")
                .build();
    }

    /**
     * Generates an HTTP-only, secure cookie for the refresh token.
     * This is the recommended way to store refresh tokens on the client-side.
     *
     * @param token      The refresh token string.
     * @param durationMs The duration for which the refresh token cookie should be valid, in milliseconds.
     * @return A {@link ResponseCookie} object for the refresh token.
     */
    public ResponseCookie generateRefreshTokenCookie(String token, Long durationMs) {
        log.debug("Generating refresh token cookie. Name: '{}', Duration: {}ms", refreshTokenCookieName, durationMs);
        return ResponseCookie.from(refreshTokenCookieName, token)
                .httpOnly(true)
                .secure(secureCookie)
                .path(cookiePath)
                .maxAge(TimeUnit.MILLISECONDS.toSeconds(durationMs))
                .sameSite("Lax") // "Strict" for more security if possible
                .build();
    }

    /**
     * Generates an HTTP-only, secure cookie that effectively clears/expires the access token cookie.
     *
     * @return A {@link ResponseCookie} object to clear the access token cookie.
     */
    public ResponseCookie getCleanAccessTokenCookie() {
        log.debug("Generating clean (expiring) access token cookie. Name: '{}'", accessTokenCookieName);
        return ResponseCookie.from(accessTokenCookieName, "")
                .path(cookiePath)
                .maxAge(0)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .build();
    }

    /**
     * Generates an HTTP-only, secure cookie that effectively clears/expires the refresh token cookie.
     * This is used during logout.
     *
     * @return A {@link ResponseCookie} object to clear the refresh token cookie.
     */
    public ResponseCookie getCleanRefreshTokenCookie() {
        log.debug("Generating clean (expiring) refresh token cookie. Name: '{}'", refreshTokenCookieName);
        return ResponseCookie.from(refreshTokenCookieName, "")
                .path(cookiePath)
                .maxAge(0)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .build();
    }
}