package za.ac.cput.domain.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpCenterUpdateDTO {

    // All fields are optional for an update.
    // The client only sends the fields they want to change.

    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters if provided")
    private String title;

    private String content; // No NotBlank, as it might not be updated. If updated, should not be blank.
    // You might add custom validation if partial updates allow blanking a field vs. not touching it.

    @Size(max = 100, message = "Category cannot exceed 100 characters if provided")
    private String category;
}