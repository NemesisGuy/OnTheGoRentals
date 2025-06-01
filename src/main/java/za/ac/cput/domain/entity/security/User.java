package za.ac.cput.domain.entity.security;

// import com.fasterxml.jackson.annotation.JsonIgnore; // Not needed if 'rentals' field is removed
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
// import za.ac.cput.domain.entity.Rental; // Not needed if 'rentals' field is removed
import za.ac.cput.domain.enums.AuthProvider;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User.java
 * Represents a user in the system, implementing Spring Security's UserDetails
 * for authentication and authorization purposes. Includes personal information,
 * roles, authentication provider details, and audit timestamps.
 * The direct collection of Rentals is removed from this entity; rentals associated
 * with a user should be queried via the RentalRepository.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date of User entity creation]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user") // Explicitly name the table
public class User implements Serializable, UserDetails {
    private static final Logger log = LoggerFactory.getLogger(User.class); // Logger for entity lifecycle

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false, unique = true, updatable = false)
    UUID uuid;

    String firstName;
    String lastName;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    // Removed @OneToMany List<Rental> rentals;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    List<Role> roles;
    String googleId;
    String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    AuthProvider authProvider;

    @Column(nullable = false)
    boolean deleted = false;

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    // Constructors
    public User(String email, String password, List<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        // Default other fields if necessary or let @PrePersist handle
    }

    public User(String firstName, String email, String password, List<Role> roles) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        // Default other fields if necessary
    }

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
            System.out.println("User Entity @PrePersist: Generated UUID: " + this.uuid + " for email: " + this.email); // For debugging
        }
        if (this.authProvider == null) {
            this.authProvider = AuthProvider.LOCAL;
        }
        if (this.roles == null) { // Initialize if service layer somehow didn't
            this.roles = new ArrayList<>();
            log.warn("User Entity @PrePersist: Roles collection was null for email: '{}'. Initialized. Service layer should assign roles.", this.email);
        }
        // Default role (e.g., USER) should be assigned by the service layer (e.g., UserServiceImpl.createUser)
        // using a fetched, managed Role entity. Adding 'new Role(...)' here can cause detached entity issues
        // if CascadeType.PERSIST is active and the Role already exists.

        this.deleted = false;
    }

    @PreUpdate
    protected void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.uuid == null) { // Should not happen on update
            this.uuid = UUID.randomUUID();
            System.err.println("User Entity @PreUpdate: UUID was null for existing user ID " + this.id + ". This is highly unusual.");
        }
        if (this.authProvider == null) { // Should not happen on update
            this.authProvider = AuthProvider.LOCAL;
            log.warn("User Entity @PreUpdate: AuthProvider was null for existing user ID {}. Defaulted to LOCAL.", this.id);

        }

    }

    // --- UserDetails Implementation ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.roles != null) {
            this.roles.forEach(role -> {
                if (role != null && role.getRoleName() != null) {
                    authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                } else {
                    log.warn("User Entity: Encountered null role or roleName while building authorities for user: {}", this.email);
                }
            });
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.deleted;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                // Avoid logging password hash
                ", roles=" + (roles != null ? roles.stream().map(Role::getRoleName).collect(Collectors.toList()) : "[]") +
                ", authProvider=" + authProvider +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}