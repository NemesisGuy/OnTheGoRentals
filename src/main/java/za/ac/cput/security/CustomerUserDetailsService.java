package za.ac.cput.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomerUserDetailsService.java
 * Implements Spring Security's {@link UserDetailsService} interface.
 * Responsible for loading user-specific data (as a {@link UserDetails} object)
 * by username (which is email in this application) from the database.
 * This service is used by Spring Security's authentication providers.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - Please specify if known]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Service // Changed from @Component to @Service as it's a service layer component
// @RequiredArgsConstructor // Can be used if you prefer Lombok for constructor
public class CustomerUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomerUserDetailsService.class); // Manual SLF4J Logger

    private final UserRepository userRepository; // Corrected variable name

    // Constructor injection is preferred
    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username (which is their email address in this application).
     * Fetches the user from the database and returns a {@link UserDetails} object
     * (our {@link User} entity implements UserDetails).
     *
     * @param email The email address (username) of the user to load.
     * @return A {@link UserDetails} object representing the user.
     * @throws UsernameNotFoundException if the user with the given email is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email (username): '{}'", email);
        User user = userRepository.findByEmailAndDeletedFalse(email) // Ensure we don't load soft-deleted users
                .orElseThrow(() -> {
                    log.warn("User not found with email: '{}'", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
        log.info("User found and loaded successfully for email: '{}'. User ID: {}", email, user.getId());
        // Your User entity must implement UserDetails for this to work directly.
        // If it doesn't, you'd map User to a UserDetails implementation here.
        // Get the roles from the user entity
        List<GrantedAuthority> authorities = user.getRoles().stream()
                // THE CHANGE IS HERE: Manually add the "ROLE_" prefix
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());
        // Set the authorities in the UserDetails object

        return user;
    }
}