package za.ac.cput.domain.dto.request;

import lombok.*;

// Feedback DTOs
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {
    private String name;
    private String comment;
}
