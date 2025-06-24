package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminContactUsUpdateDTO { // Specific for admin update capabilities
    // Fields an admin might be allowed to update
    private String title;
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format if provided")
    private String email;
    private String subject;
    @Size(min = 10, message = "Message must be at least 10 characters long if provided")
    private String message;
    // Admin might also manage a 'status' or 'respondedTo' flag if you add it to the entity
    // private Boolean respondedTo;
}