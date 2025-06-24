package za.ac.cput.domain.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsResponseDTO {
    private UUID uuid;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    private String message;
}