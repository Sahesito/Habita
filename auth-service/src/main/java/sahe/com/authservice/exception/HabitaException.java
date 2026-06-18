package sahe.com.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HabitaException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public HabitaException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public static HabitaException unauthorized(String message) {
        return new HabitaException("UNAUTHORIZED", message, HttpStatus.UNAUTHORIZED);
    }

    public static HabitaException forbidden(String message) {
        return new HabitaException("FORBIDDEN", message, HttpStatus.FORBIDDEN);
    }

    public static HabitaException notFound(String message) {
        return new HabitaException("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }

    public static HabitaException badRequest(String message) {
        return new HabitaException("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }

    public static HabitaException conflict(String message) {
        return new HabitaException("CONFLICT", message, HttpStatus.CONFLICT);
    }
}