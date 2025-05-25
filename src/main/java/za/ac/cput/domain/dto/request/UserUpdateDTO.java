package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.AuthProvider;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO { // Can be used by admin and potentially by user for their own profile (subset of fields)
    // All fields are optional for an update.
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email(message = "Email should be valid if provided")
    @Size(max = 100)
    private String email; // Changing email (username) can be complex, service handles uniqueness

    @Size(min = 6, message = "New password must be at least 6 characters if provided")
    private String password; // New plain text password, service will encode if provided

    private List<String> roleNames; // List of role names to set/replace

    private AuthProvider authProvider; // Admin might change this
    private String googleId;           // Admin might manage this link
    private String profileImageUrl;
    private Boolean deleted;           // Admin can soft-delete/undelete
    // private Boolean accountLocked;  // If you add account locking
    // private Boolean enabled;        // If you add an enabled flag separate from 'deleted'
}