package za.ac.cput.api.advice;

import org.springframework.dao.DataIntegrityViolationException; // For DB constraint errors
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import za.ac.cput.api.response.ApiResponse;
import za.ac.cput.api.response.FieldErrorDto;
import za.ac.cput.exception.ResourceNotFoundException; // Your custom exception
import za.ac.cput.exception.BadRequestException;     // Your custom exception

import java.util.Collections; // For List.of() if not Java 9+ or for Collections.singletonList
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldErrorDto> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        // For validation errors, data is typically null or not applicable
        return new ResponseEntity<>(new ApiResponse<>(errorList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        FieldErrorDto error = new FieldErrorDto("resource", ex.getMessage());
        // For resource not found, data is null
        return new ResponseEntity<>(new ApiResponse<>(Collections.singletonList(error)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class) // Your custom bad request exception
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        FieldErrorDto error = new FieldErrorDto("request", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(Collections.singletonList(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // You might want to parse ex.getCause() or ex.getMostSpecificCause() for more specific DB errors
        // For example, to detect unique constraint violations
        String message = "Database error: A data integrity rule was violated.";
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("duplicate entry")) {
            message = "Database error: This item already exists or a unique field is duplicated.";
        }
        FieldErrorDto error = new FieldErrorDto("database", message);
        // Log the full exception for server-side debugging:
        // log.error("DataIntegrityViolationException: ", ex);
        return new ResponseEntity<>(new ApiResponse<>(Collections.singletonList(error)), HttpStatus.CONFLICT); // 409 Conflict is often suitable
    }

    @ExceptionHandler(SecurityException.class) // e.g., for unauthorized actions
    public ResponseEntity<ApiResponse<Object>> handleSecurityException(SecurityException ex) {
        FieldErrorDto error = new FieldErrorDto("authorization", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(Collections.singletonList(error)), HttpStatus.FORBIDDEN); // 403 Forbidden
    }


    // Generic fallback handler for other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        // Log the full exception for server-side debugging
        // log.error("An unexpected error occurred: ", ex);
        System.err.println("UNEXPECTED EXCEPTION: " + ex.getMessage());
        ex.printStackTrace(); // For dev; in prod, log to a file/service

        FieldErrorDto error = new FieldErrorDto("general", "An unexpected internal server error occurred.");
        return new ResponseEntity<>(new ApiResponse<>(Collections.singletonList(error)), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}