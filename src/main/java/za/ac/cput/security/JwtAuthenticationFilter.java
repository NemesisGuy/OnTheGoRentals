package za.ac.cput.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter.java
 * A Spring Security filter that intercepts incoming requests to validate JWT access tokens.
 * If a valid token is found in the Authorization header, it authenticates the user
 * and sets the {@link org.springframework.security.core.Authentication} object in the
 * {@link SecurityContextHolder}. This filter runs once per request.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Component
// @RequiredArgsConstructor from Lombok can be used for constructor injection
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // Manual SLF4J Logger

    private final JwtUtilities jwtUtilities;
    private final CustomerUserDetailsService customerUserDetailsService;

    // Constructor injection is preferred
    public JwtAuthenticationFilter(JwtUtilities jwtUtilities, CustomerUserDetailsService customerUserDetailsService) {
        this.jwtUtilities = jwtUtilities;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    /**
     * Processes an incoming HTTP request to check for and validate a JWT access token.
     * If the token is valid, it sets up Spring Security's context with the authenticated user.
     *
     * @param request     The incoming {@link HttpServletRequest}.
     * @param response    The outgoing {@link HttpServletResponse}.
     * @param filterChain The filter chain to pass the request along.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        log.trace("JwtAuthenticationFilter processing request for URI: {}", requestURI);

        // Skip JWT processing for auth endpoints like /login, /register, /refresh
        // (These should be explicitly permitted in SecurityFilterChain)
        // if (requestURI.startsWith("/api/v1/auth/")) {
        //     log.trace("Skipping JWT filter for auth path: {}", requestURI);
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        String token = jwtUtilities.getToken(request);

        if (token != null) {
            log.debug("Token found in request to URI: {}", requestURI);
            if (jwtUtilities.validateToken(token)) { // Validates signature and expiration
                String email = jwtUtilities.extractUserEmail(token);
                log.debug("Token validated successfully. Email extracted: {}", email);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.trace("Security context is null for user: {}. Attempting to load UserDetails.", email);
                    UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);

                    // Double check token against UserDetails (e.g., if userDetails could have changed since token issuance)
                    if (jwtUtilities.validateToken(token, userDetails)) { // This check is good practice
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        // Set details from the request (e.g., IP address, session ID if any)
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.info("User '{}' successfully authenticated and security context updated for URI: {}", email, requestURI);
                    } else {
                        log.warn("Token was structurally valid but failed UserDetails validation for user '{}' and URI: {}", email, requestURI);
                    }
                } else {
                    if (email == null)
                        log.warn("Email could not be extracted from a structurally valid token for URI: {}", requestURI);
                    if (SecurityContextHolder.getContext().getAuthentication() != null)
                        log.trace("Security context already populated for URI: {}", requestURI);
                }
            } else {
                log.debug("Token validation failed (e.g., expired, invalid signature) for token found in request to URI: {}", requestURI);
            }
        } else {
            log.trace("No JWT token found in Authorization header for URI: {}", requestURI);
        }
        filterChain.doFilter(request, response);
    }
}