package za.ac.cput.domain.dto.request;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
// ContactUs DTOs
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ContactUsRequestDTO {
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    private String message;
}