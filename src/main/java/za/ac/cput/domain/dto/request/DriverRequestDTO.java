package za.ac.cput.domain.dto.request;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DriverRequestDTO {
    private String firstName;
    private String lastName;
    private String licenseCode;
}
