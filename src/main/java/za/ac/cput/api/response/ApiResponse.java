package za.ac.cput.api.response;

import lombok.Getter; // Import Lombok Getter
import lombok.NoArgsConstructor; // Import NoArgsConstructor
import lombok.Setter; // Import Lombok Setter (optional if only for serialization)

import java.util.Collections; // For empty list
import java.util.List;

@Getter // Add Lombok's @Getter for all fields
@Setter // Add Lombok's @Setter if you ever need to deserialize this or for completeness
@NoArgsConstructor // Ensures a no-argument constructor exists (good for Jackson)
public class ApiResponse<T> {
    private T data;
    private List<FieldErrorDto> errors;
    private String status;

    // Constructor for successful response
    public ApiResponse(T data) {
        this.data = data;
        this.status = "success";
        this.errors = Collections.emptyList(); // Use Collections.emptyList() for an immutable empty list
    }

    // Constructor for error response
    public ApiResponse(List<FieldErrorDto> errors) {
        this.errors = errors;
        this.status = "fail"; // Or "error" depending on your convention
        this.data = null; // Explicitly null for error responses
    }

    // If not using Lombok, you would manually write:
    // public T getData() { return data; }
    // public void setData(T data) { this.data = data; }
    // public List<FieldErrorDto> getErrors() { return errors; }
    // public void setErrors(List<FieldErrorDto> errors) { this.errors = errors; }
    // public String getStatus() { return status; }
    // public void setStatus(String status) { this.status = status; }
}