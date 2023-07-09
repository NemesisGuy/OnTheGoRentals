package za.ac.cput.exception;

public class UserCantRentMoreThanOneCarException extends RuntimeException {
    public UserCantRentMoreThanOneCarException(String message) {
        super(message);
    }
}
