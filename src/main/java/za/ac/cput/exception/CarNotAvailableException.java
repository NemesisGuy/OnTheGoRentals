package za.ac.cput.exception;

/**
 * Author: Peter Buckingham (220165289)
 */
public class CarNotAvailableException extends RuntimeException {
    public CarNotAvailableException(String message) {
        super(message);
    }
}
