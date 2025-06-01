package za.ac.cput.domain.entity.security;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

/**
 * Role.java
 * Represents a user role within the application (e.g., USER, ADMIN, SUPERADMIN).
 * Roles are defined by the {@link RoleName} enum and are used for authorization.
 *
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date of Role entity creation]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-30
 */
@Entity
@Getter
@Setter // Setters are useful for JPA, or if you modify roleName (though typically fixed after creation)
@NoArgsConstructor
@AllArgsConstructor // Useful for @Builder
@Builder // Added for consistency if you use builders elsewhere for Role
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "role") // Explicit table name
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    /**
     * The name of the role, derived from the {@link RoleName} enum.
     * This is stored as a string in the database and must be unique.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30) // Ensure length accommodates longest RoleName
    private RoleName roleName;

    /**
     * Constructs a Role from a string representation of the role name.
     * This string will be converted to a {@link RoleName} enum constant.
     *
     * @param roleNameString The string name of the role (e.g., "USER", "ADMIN").
     * @throws IllegalArgumentException if the roleNameString does not match a valid RoleName.
     */
    public Role(String roleNameString) {
        if (roleNameString == null || roleNameString.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name string cannot be null or empty.");
        }
        this.roleName = RoleName.valueOf(roleNameString.trim().toUpperCase());
    }

    /**
     * Constructs a Role directly from a {@link RoleName} enum constant.
     *
     * @param roleName The RoleName enum constant.
     */
    public Role(RoleName roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("RoleName enum constant cannot be null.");
        }
        this.roleName = roleName;
    }

    /**
     * Gets the string representation of the role name.
     *
     * @return The name of the role as a String.
     */
    public String getRoleName() { // Custom getter to ensure String return if needed, though Lombok @Getter on enum field works
        return roleName != null ? roleName.name() : null;
    }
    public RoleName getRoleNameEnum() { // Add this getter
        return this.roleName;
    }

    // Lombok @Getter provides getRoleName() that returns RoleName enum.
    // If you specifically need a method that always returns the String value, the above is fine.
    // Otherwise, just use the Lombok-generated getRoleName().

   /* @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        // If persisted, ID is the best check. Otherwise, roleName.
        if (id != null && role.id != null) {
            return Objects.equals(id, role.id);
        }
        return roleName == role.roleName;
    }*/
   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       Role role = (Role) o;
       // Use the business key (roleName) for equality.
       // RoleName is an enum, so Objects.equals handles nulls correctly,
       // and enums can be compared by reference or .equals().
       return Objects.equals(roleName, role.roleName);
   }

    @Override
    public int hashCode() {
        // Consistent with equals
        return Objects.hash(roleName);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName=" + roleName +
                '}';
    }
}