package za.ac.cput.api.response;

import java.util.List;

public class ApiResponse<T> {
    private T data;
    private List<FieldErrorDto> errors;
    private String status;

    public ApiResponse() {}

    public ApiResponse(T data) {
        this.data = data;
        this.status = "success";
        this.errors = List.of();
    }

    public ApiResponse(List<FieldErrorDto> errors) {
        this.errors = errors;
        this.status = "fail";
        this.data = null;
    }

    // Getters and setters
}
