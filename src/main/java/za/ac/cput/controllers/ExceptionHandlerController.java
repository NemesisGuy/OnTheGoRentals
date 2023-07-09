package za.ac.cput.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(CarNotAvailableException.class)
    public ResponseEntity<String> handleCarNotAvailableException(CarNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserCantRentMoreThanOneCarException.class)
    public ResponseEntity<String> handleUserCantRentMoreThanOneCarException(UserCantRentMoreThanOneCarException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
