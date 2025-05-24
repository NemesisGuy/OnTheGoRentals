package za.ac.cput.domain.dto.response;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DriverResponseDTO {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String licenseCode;
}