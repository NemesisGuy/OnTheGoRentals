package za.ac.cput.domain.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DriverCreateDTO {
    private String firstName;
    private String lastName;
    private String licenseCode;


}