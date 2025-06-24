package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCreateDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Comment is required")
    private String comment;
}