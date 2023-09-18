package za.ac.cput.exception;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
public class UserCantRentMoreThanOneCarException extends RuntimeException {
    public UserCantRentMoreThanOneCarException(String message) {
        super(message);
    }
}
