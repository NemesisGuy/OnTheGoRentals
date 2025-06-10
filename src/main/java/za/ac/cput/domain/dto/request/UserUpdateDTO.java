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
public class UserUpdateDTO {
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")

    private String lastName;

    @Email(message = "Email should be valid if provided")
    @Size(max = 100)
    private String email;

    @Size(min = 6, message = "New password must be at least 6 characters if provided")
    private String password;

    private List<String> roleNames;

    private AuthProvider authProvider;
    private String googleId;
    private Boolean deleted;
    // REMOVED profileImageUrl - this is handled by a separate file upload endpoint
}