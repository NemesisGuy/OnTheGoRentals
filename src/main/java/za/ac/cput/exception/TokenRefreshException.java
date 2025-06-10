package za.ac.cput.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // Or HttpStatus.UNAUTHORIZED depending on context
public class TokenRefreshException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for token [%s]: %s", token.substring(0, Math.min(token.length(), 10)) + "...", message));
    }

    public TokenRefreshException(String message) {
        super(message);
    }
}