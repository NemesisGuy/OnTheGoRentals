package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.AuthProvider; // If admin can set this

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO { // Generic name, context of use (admin) defines its power
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // Plain text password, service will encode

    @NotEmpty(message = "User must have at least one role")
    private List<String> roleNames; // Admin provides role names as strings

    // Optional fields admin might set:
    private AuthProvider authProvider;
    private String googleId;
    private String profileImageUrl;
    // 'deleted' is usually false by default for new users.
}