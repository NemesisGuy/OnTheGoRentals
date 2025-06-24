package za.ac.cput.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * ApiResponse.java
 * A generic wrapper for API responses to provide a consistent structure.
 * It includes fields for the actual data payload, a list of errors (if any),
 * and a status indicator ("success" or "fail").
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: 2025-05-18
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@Getter
@Setter
@NoArgsConstructor // For Jackson deserialization and general flexibility
public class ApiResponseWrapper<T> {
    private T data;
    private List<FieldErrorDto> errors;
    private String status; // e.g., "success", "fail", "error"

    /**
     * Constructor for a successful response.
     *
     * @param data The data payload of the response.
     */
    public ApiResponseWrapper(T data) {
        this.data = data;
        this.status = "success"; // Convention for successful responses
        this.errors = Collections.emptyList(); // No errors for a successful response
    }

    /**
     * Constructor for an error response.
     *
     * @param errors A list of {@link FieldErrorDto} detailing the errors.
     */
    public ApiResponseWrapper(List<FieldErrorDto> errors) {
        this.data = null; // No data payload for an error response
        this.status = "fail"; // Convention for responses with errors (e.g., validation, not found)
        // Could also be "error" for unexpected server errors.
        this.errors = (errors != null) ? errors : Collections.emptyList();
    }

    /**
     * Constructor for an error response with a custom status string.
     *
     * @param errors A list of {@link FieldErrorDto} detailing the errors.
     * @param status A custom status string (e.g., "error" for server errors).
     */
    public ApiResponseWrapper(List<FieldErrorDto> errors, String status) {
        this.data = null;
        this.status = status;
        this.errors = (errors != null) ? errors : Collections.emptyList();
    }
}