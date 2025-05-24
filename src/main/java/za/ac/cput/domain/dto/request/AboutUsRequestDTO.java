package za.ac.cput.domain.dto.request;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// AboutUs DTOs
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AboutUsRequestDTO {
    private String address;
    private String officeHours;
    private String email;
    private String telephone;
    private String whatsApp;
}
