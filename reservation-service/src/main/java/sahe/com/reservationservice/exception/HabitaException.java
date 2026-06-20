package sahe.com.reservationservice.exception;

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

    public static HabitaException notFound(String message) {
        return new HabitaException("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }

    public static HabitaException badRequest(String message) {
        return new HabitaException("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }

    public static HabitaException conflict(String message) {
        return new HabitaException("TIME_SLOT_UNAVAILABLE", message, HttpStatus.CONFLICT);
    }
}