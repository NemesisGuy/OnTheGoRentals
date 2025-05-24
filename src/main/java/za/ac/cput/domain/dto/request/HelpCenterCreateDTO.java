package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpCenterCreateDTO {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content; // Could be TEXT in DB, so less strict size here unless needed

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category; // Optional, or could be an enum if categories are fixed
}