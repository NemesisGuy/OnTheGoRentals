package za.ac.cput.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FieldErrorDto.java
 * A Data Transfer Object representing a specific field error, typically used
 * in API error responses to detail validation failures or other contextual errors.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: 2025-05-08
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Getter
@Setter
@NoArgsConstructor // Added for Jackson/framework flexibility
public class FieldErrorDto {
    private String field;   // The name of the field that caused the error, or a general category.
    private String message; // The error message.

    /**
     * Constructs a new FieldErrorDto.
     *
     * @param field   The name of the field associated with the error or a general error category.
     * @param message The descriptive error message.
     */
    public FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }
}