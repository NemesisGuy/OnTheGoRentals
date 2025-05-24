package za.ac.cput.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorDto {
    private String field;
    private String message;

    public FieldErrorDto() {}

    public FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }

    // Getters and setters
}
