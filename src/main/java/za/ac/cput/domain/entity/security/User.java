package za.ac.cput.domain.entity.security;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
 * Represents a user in the system, implementing Spring Security's UserDetails.
 * Now includes fields for managing a user's profile image.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
public class User implements Serializable, UserDetails {
    private static final Logger log = LoggerFactory.getLogger(User.class);

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    List<Role> roles;

    String googleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    AuthProvider authProvider;

    // New fields for profile image tracking
    String profileImageFileName;    // e.g., "user_selfie_abc.jpg"
    String profileImageType;        // e.g., "selfies" from ImageType enum
    LocalDateTime profileImageUploadedAt;

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
    }

    public User(String firstName, String email, String password, List<Role> roles) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
    }

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.uuid == null) this.uuid = UUID.randomUUID();
        if (this.authProvider == null) this.authProvider = AuthProvider.LOCAL;
        if (this.roles == null) this.roles = new ArrayList<>();
        this.deleted = false;
    }

    @PreUpdate
    protected void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- UserDetails Implementation ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Implementation remains the same
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
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
                ", roles=" + (roles != null ? roles.stream().map(Role::getRoleName).collect(Collectors.toList()) : "[]") +
                ", profileImageFileName='" + profileImageFileName + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}