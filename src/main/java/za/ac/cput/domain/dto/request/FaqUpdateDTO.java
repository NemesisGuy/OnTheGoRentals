package za.ac.cput.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqUpdateDTO {
    // Fields are optional for update. Client sends only what needs to change.
    private String question;
    private String answer;
}