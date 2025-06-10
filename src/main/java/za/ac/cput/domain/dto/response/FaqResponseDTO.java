package za.ac.cput.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqResponseDTO {
    private UUID uuid;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}