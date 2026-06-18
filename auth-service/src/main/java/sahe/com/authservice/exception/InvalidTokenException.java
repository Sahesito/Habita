package sahe.com.authservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends HabitaException {

    public InvalidTokenException(String message) {
        super("INVALID_TOKEN", message, HttpStatus.UNAUTHORIZED);
    }

    public static InvalidTokenException expired() {
        return new InvalidTokenException("Token has expired");
    }

    public static InvalidTokenException revoked() {
        return new InvalidTokenException("Token has been revoked");
    }

    public static InvalidTokenException invalid() {
        return new InvalidTokenException("Token is invalid");
    }
}