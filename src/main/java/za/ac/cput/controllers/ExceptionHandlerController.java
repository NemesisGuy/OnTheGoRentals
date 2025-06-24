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
 * for specific custom exceptions thrown by REST controllers. This class intercepts exceptions
 * and returns standardized, plain-text error messages with appropriate HTTP status codes.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestControllerAdvice(basePackages = "za.ac.cput.controllers")
public class ExceptionHandlerController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    /**
     * Handles {@link CarNotAvailableException}.
     * This is triggered when a user tries to book a car that is not currently available.
     * Responds with HTTP 409 Conflict, as this is a business rule conflict.
     *
     * @param ex The caught {@link CarNotAvailableException}.
     * @return A ResponseEntity containing the error message and HTTP status 409.
     */
    @ExceptionHandler(CarNotAvailableException.class)
    public ResponseEntity<String> handleCarNotAvailableException(CarNotAvailableException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: CarNotAvailableException caught: {}", requesterId, ex.getMessage());
        // Using 409 Conflict is more semantically correct for a business rule violation.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handles {@link UserCantRentMoreThanOneCarException}.
     * This is triggered when a user who already has an active rental tries to book another car.
     * Responds with HTTP 409 Conflict.
     *
     * @param ex The caught {@link UserCantRentMoreThanOneCarException}.
     * @return A ResponseEntity containing the error message and HTTP status 409.
     */
    @ExceptionHandler(UserCantRentMoreThanOneCarException.class)
    public ResponseEntity<String> handleUserCantRentMoreThanOneCarException(UserCantRentMoreThanOneCarException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: UserCantRentMoreThanOneCarException caught: {}", requesterId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handles {@link ResourceNotFoundException}.
     * This is a generic handler for any time a requested entity (like a Car, User, or Booking) is not found.
     * Responds with HTTP 404 Not Found.
     *
     * @param ex The caught {@link ResourceNotFoundException}.
     * @return A ResponseEntity containing the error message and HTTP status 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: ResourceNotFoundException caught: {}", requesterId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link RoleNotFoundException}.
     * This is triggered during user management if a specified role does not exist.
     * Responds with HTTP 404 Not Found.
     *
     * @param ex The caught {@link RoleNotFoundException}.
     * @return A ResponseEntity containing the error message and HTTP status 404.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: RoleNotFoundException caught: {}", requesterId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link UnauthorisedException}.
     * This is a custom exception for business-level authorization failures.
     * Responds with HTTP 403 Forbidden.
     *
     * @param ex The caught {@link UnauthorisedException}.
     * @return A ResponseEntity containing the error message and HTTP status 403.
     */
    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<String> handleUnauthorisedException(UnauthorisedException ex) {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        log.warn("Requester [{}]: UnauthorisedException caught: {}", requesterId, ex.getMessage());
        // 403 Forbidden is more appropriate for when the user is known but lacks permission.
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}