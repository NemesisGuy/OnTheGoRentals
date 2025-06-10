package za.ac.cput.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {
    private String firstName;
    private String lastName;
    private String licenseCode;
}
