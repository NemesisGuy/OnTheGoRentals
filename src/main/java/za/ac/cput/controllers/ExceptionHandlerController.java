package za.ac.cput.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import za.ac.cput.exception.*;
import za.ac.cput.utils.SecurityUtils;

/**
 * ExceptionHandlerController.java
 * A {@link RestControllerAdvice} component that provides centralized exception handling
 * for specific custom exceptions thrown by REST controllers.
 * These handlers currently return plain text error messages with appropriate HTTP status codes.
 * For more structured JSON error responses, see {@link za.ac.cput.api.advice.GlobalExceptionHandler}.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: [Original Date - If known, otherwise assume similar to ErrorController]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestControllerAdvice(basePackages = "za.ac.cput.controllers") // Ensure this targets the correct controller packages
public class ExceptionHandlerController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    /**
     * Handles {@link CarNotAvailableException}.
     * Responds with HTTP 400 Bad Request and the exception message.
     *
     * @param ex The caught {@link CarNotAvailableException}.
     * @return A ResponseEntity containing the error message and HTTP status.
     */
    @ExceptionHandler(CarNotAvailableException.class)
    public ResponseEntity<String> handleCarNotAvailableException(CarNotAvailableException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: CarNotAvailableException caught: {}", requesterId, ex.getMessage(), ex); // Log with exception for stack trace
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles {@link UserCantRentMoreThanOneCarException}.
     * Responds with HTTP 400 Bad Request and the exception message.
     *
     * @param ex The caught {@link UserCantRentMoreThanOneCarException}.
     * @return A ResponseEntity containing the error message and HTTP status.
     */
    @ExceptionHandler(UserCantRentMoreThanOneCarException.class)
    public ResponseEntity<String> handleUserCantRentMoreThanOneCarException(UserCantRentMoreThanOneCarException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: UserCantRentMoreThanOneCarException caught: {}", requesterId, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles {@link ResourceNotFoundException}.
     * Responds with HTTP 404 Not Found and the exception message.
     *
     * @param ex The caught {@link ResourceNotFoundException}.
     * @return A ResponseEntity containing the error message and HTTP status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: ResourceNotFoundException caught: {}", requesterId, ex.getMessage()); // Stack trace might be less critical for 404s but can be added
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link RoleNotFoundException}.
     * Responds with HTTP 404 Not Found and the exception message.
     *
     * @param ex The caught {@link RoleNotFoundException}.
     * @return A ResponseEntity containing the error message and HTTP status.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: RoleNotFoundException caught: {}", requesterId, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link UnauthorisedException}.
     * Responds with HTTP 401 Unauthorized and the exception message.
     *
     * @param ex The caught {@link UnauthorisedException}.
     * @return A ResponseEntity containing the error message and HTTP status.
     */
    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<String> handleUnauthorisedException(UnauthorisedException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: UnauthorisedException caught: {}", requesterId, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}