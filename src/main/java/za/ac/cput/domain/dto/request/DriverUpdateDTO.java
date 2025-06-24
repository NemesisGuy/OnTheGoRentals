package za.ac.cput.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateDTO {
    // Include only fields that can be updated
    // All fields are optional in an update DTO typically
    private String firstName;
    private String lastName;
    private String licenseCode;
}