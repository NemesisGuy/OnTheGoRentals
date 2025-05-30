package za.ac.cput.domain.entity.security;
/**
 * Author: Peter Buckingham (220165289)
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.enums.AuthProvider;
/*import za.ac.cput.utils.GrantedAuthorityDeserializer;
import za.ac.cput.utils.GrantedAuthoritySerializer;*/

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
/*@ToString*/
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
/*@JsonSerialize(using = GrantedAuthoritySerializer.class)
@JsonDeserialize(using = GrantedAuthorityDeserializer.class)*/
public class User implements Serializable, UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    String firstName;
    String lastName;
    String email;
    String password;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Rental> rentals = new ArrayList<>();

    //A user can have multiple roles
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    List<Role> roles;
    private String googleId; // To store the unique Google User ID (the 'sub' claim)
    private String profileImageUrl; // To store the profile picture URL from Google
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider; // Enum: LOCAL, GOOGLE, etc.
    @Column(nullable = false)
    private boolean deleted = false;



    public User(String email, String password, List<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(String firstName, String email, String password, List<Role> roles) {

        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
        return authorities;
    }

    public Integer getId() {
        return this.id;
    }
    public UUID getUuid() {
        return this.uuid;
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
        return !isDeleted();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", googleId='" + googleId + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", authProvider=" + authProvider +
                ", deleted=" + deleted +
                '}';
    }
    // Inside za.ac.cput.domain.security.User.java

    @PrePersist
    @PreUpdate
    protected void initializeDefaults() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
            System.out.println("User Entity @PrePersist: Generated UUID: " + this.uuid); // For debugging
        }
        if (this.authProvider == null) {
            this.authProvider = AuthProvider.LOCAL;
        }
        if (this.roles == null || this.roles.isEmpty()) {
            this.roles = new ArrayList<>();
            this.roles.add(new Role("USER")); // Ensure "USER" exists in your database
        }
    }


}

