package za.ac.cput.domain.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqRequestDTO {
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}