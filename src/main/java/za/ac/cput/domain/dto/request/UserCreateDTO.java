package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.ac.cput.domain.enums.AuthProvider;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
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
    private String password;

    @NotEmpty(message = "User must have at least one role")
    private List<String> roleNames;

    private AuthProvider authProvider;
    private String googleId;
    // REMOVED profileImageUrl - this is handled by a separate file upload endpoint
}