package za.ac.cput.api.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import za.ac.cput.api.response.ApiResponseWrapper;
import za.ac.cput.api.response.FieldErrorDto;
import za.ac.cput.exception.*;
import za.ac.cput.utils.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler.java
 * A {@link RestControllerAdvice} component that provides centralized exception handling
 * for REST controllers. It catches various exceptions and transforms them into a
 * standardized {@link ApiResponseWrapper} format with appropriate HTTP status codes.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: 2025-04-08
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestControllerAdvice(basePackages = "za.ac.cput.controllers") // Ensure this targets REST controllers
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link MethodArgumentNotValidException} (validation errors from @Valid).
     * Responds with HTTP 400 Bad Request and a list of field-specific validation errors.
     *
     * @param ex The caught {@link MethodArgumentNotValidException}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        List<FieldErrorDto> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.warn("Requester [{}]: Validation failed. Errors: {}", requesterId, errorList, ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(errorList), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom {@link ResourceNotFoundException}.
     * Responds with HTTP 404 Not Found and the exception message.
     *
     * @param ex The caught {@link ResourceNotFoundException}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with the error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("resource", ex.getMessage());
        log.warn("Requester [{}]: ResourceNotFoundException: {}", requesterId, ex.getMessage()); // Stack trace usually not needed for 404 unless debugging
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles custom {@link BadRequestException}.
     * Responds with HTTP 400 Bad Request and the exception message.
     *
     * @param ex The caught {@link BadRequestException}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with the error.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleBadRequestException(BadRequestException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("request", ex.getMessage());
        log.warn("Requester [{}]: BadRequestException: {}", requesterId, ex.getMessage(), ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link DataIntegrityViolationException} (e.g., DB unique constraint violations).
     * Responds with HTTP 409 Conflict and a generic database error message.
     *
     * @param ex The caught {@link DataIntegrityViolationException}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with the error.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        String message = "Database error: A data integrity rule was violated.";
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("duplicate entry")) {
            message = "Database error: This item already exists or a unique field is duplicated.";
        } else if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("constraint violation")) {
            // Could add more specific parsing here for other constraint types if needed
            message = "Database error: A database constraint was violated (e.g., foreign key).";
        }
        FieldErrorDto error = new FieldErrorDto("database", message);
        log.error("Requester [{}]: DataIntegrityViolationException: {}", requesterId, message, ex); // Log full exception
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.CONFLICT);
    }

    /**
     * Handles {@link AccessDeniedException} from Spring Security (typically results in 403).
     *
     * @param ex The caught {@link AccessDeniedException}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with an authorization error.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("authorization", "Access denied. You do not have permission to perform this action.");
        log.warn("Requester [{}]: AccessDeniedException: {}", requesterId, ex.getMessage()); // Message might be generic
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.FORBIDDEN);
    }

    // --- Handlers for other custom exceptions previously in ExceptionHandlerController for ApiResponse consistency ---

    @ExceptionHandler(CarNotAvailableException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleCarNotAvailableApi(CarNotAvailableException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("car.availability", ex.getMessage());
        log.warn("Requester [{}]: CarNotAvailableException: {}", requesterId, ex.getMessage(), ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserCantRentMoreThanOneCarException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleUserCantRentMoreThanOneCarApi(UserCantRentMoreThanOneCarException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("rental.limit", ex.getMessage());
        log.warn("Requester [{}]: UserCantRentMoreThanOneCarException: {}", requesterId, ex.getMessage(), ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleRoleNotFoundApi(RoleNotFoundException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("role", ex.getMessage());
        log.warn("Requester [{}]: RoleNotFoundException: {}", requesterId, ex.getMessage(), ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorisedException.class) // Your custom one
    public ResponseEntity<ApiResponseWrapper<Object>> handleUnauthorisedApi(UnauthorisedException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        FieldErrorDto error = new FieldErrorDto("authentication", ex.getMessage());
        log.warn("Requester [{}]: UnauthorisedException: {}", requesterId, ex.getMessage(), ex);
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.UNAUTHORIZED);
    }

    // Generic fallback handler

    /**
     * Handles all other unclassified {@link Exception}s.
     * Responds with HTTP 500 Internal Server Error and a generic error message.
     * It's crucial to log the full exception here for debugging.
     *
     * @param ex The caught {@link Exception}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with a generic server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleGenericException(Exception ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.error("Requester [{}]: An unexpected error occurred: {}", requesterId, ex.getMessage(), ex); // Log full exception

        FieldErrorDto error = new FieldErrorDto("general", "An unexpected internal server error occurred. Please try again later.");
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles custom {@link InvalidDateRangeException}.
     * Responds with HTTP 400 Bad Request and the exception message.
     *
     * @param ex The caught {@link InvalidDateRangeException}.
     * @return A ResponseEntity containing an {@link ApiResponseWrapper} with the error.
     */
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleInvalidDateRangeException(InvalidDateRangeException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier(); // If you have this utility
        log.warn("Requester [{}]: InvalidDateRangeException: {}", requesterId, ex.getMessage());
        FieldErrorDto error = new FieldErrorDto("dateRange", ex.getMessage());
        return new ResponseEntity<>(new ApiResponseWrapper<>(Collections.singletonList(error)), HttpStatus.BAD_REQUEST);
    }
}