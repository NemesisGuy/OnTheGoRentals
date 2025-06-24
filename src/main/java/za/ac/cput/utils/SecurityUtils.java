// In your za.ac.cput.utils package (or similar)
package za.ac.cput.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    private static final String ANONYMOUS_USER_PRINCIPAL = "anonymousUser";
    private static final String DEFAULT_GUEST_IDENTIFIER = "GUEST";

    /**
     * Gets an identifier for the currently authenticated user.
     * Returns a default guest identifier if the user is anonymous or not authenticated.
     *
     * @return String representing the user identifier (e.g., email, username) or "GUEST".
     */
    public static String getRequesterIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || ANONYMOUS_USER_PRINCIPAL.equals(authentication.getPrincipal())) {
            return DEFAULT_GUEST_IDENTIFIER;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Standard Spring Security username
        } else if (principal instanceof String) {
            return (String) principal; // If Authentication.getName() or principal is a simple string
        }
        // Fallback to whatever getName() returns if it's not a UserDetails or String
        // This could be your custom principal's string representation.
        // If getName() returns null for an authenticated user (unlikely but possible with custom setups),
        // you might want another fallback.
        String name = authentication.getName();
        return (name != null) ? name : "AUTHENTICATED_USER_UNKNOWN_NAME";
    }
}