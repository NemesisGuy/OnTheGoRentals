package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DriverCreateDTO {

    @NotBlank(message = "First name cannot be null")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;
    @NotBlank(message = "Last name cannot be null")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    @NotBlank(message = "License code cannot be null")
    @Size(max = 20, message = "License code cannot exceed 20 characters")
    private String licenseCode;


}