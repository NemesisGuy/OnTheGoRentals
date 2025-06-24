package za.ac.cput.domain.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutUsResponseDTO {
    private UUID uuid;
    private String address;
    private String officeHours;
    private String email;
    private String telephone;
    private String whatsApp;
}