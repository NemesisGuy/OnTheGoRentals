package za.ac.cput.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {
    private UUID uuid;
    private String name;
    private String comment;
    private LocalDateTime createdAt; // Added createdAt to response

}
