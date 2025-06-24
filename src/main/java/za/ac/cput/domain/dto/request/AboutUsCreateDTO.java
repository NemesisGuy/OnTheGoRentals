package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// @Builder // Builder is less common for request DTOs unless they have many optional fields
public class AboutUsCreateDTO {

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotBlank(message = "Office hours cannot be blank")
    @Size(min = 5, max = 100, message = "Office hours must be between 5 and 100 characters")
    private String officeHours;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Telephone number cannot exceed 20 characters")
    private String telephone; // Optional, but if provided, should adhere to size

    @Size(max = 20, message = "WhatsApp number cannot exceed 20 characters")
    private String whatsApp;  // Optional
}