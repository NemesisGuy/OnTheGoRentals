package za.ac.cput.domain.dto.request;

import lombok.*;

// ContactUs DTOs
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsRequestDTO {
    private String title;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;
    private String message;
}