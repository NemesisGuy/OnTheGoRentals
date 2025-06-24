package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// import java.util.UUID; // Only if you were to send UUID in the body for identification

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutUsUpdateDTO {

    // For updates, fields are typically optional. The client sends only what they want to change.
    // Validation still applies if a field IS provided.

    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters if provided")
    private String address;

    @Size(min = 5, max = 100, message = "Office hours must be between 5 and 100 characters if provided")
    private String officeHours;

    @Email(message = "Email should be a valid email address if provided")
    @Size(max = 100, message = "Email cannot exceed 100 characters if provided")
    private String email;

    @Size(max = 20, message = "Telephone number cannot exceed 20 characters if provided")
    private String telephone;

    @Size(max = 20, message = "WhatsApp number cannot exceed 20 characters if provided")
    private String whatsApp;

    // The UUID for identification is typically passed as a path variable in the PUT request,
    // so it's not usually needed in the update DTO body unless your API design dictates it.
    // private UUID uuid;
}