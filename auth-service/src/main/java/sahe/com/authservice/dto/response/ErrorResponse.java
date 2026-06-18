package sahe.com.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ErrorResponse {

    private String error;
    private String message;
    private int status;
    private Instant timestamp;

    @JsonProperty("trace_id")
    private String traceId;

    public static ErrorResponse of(String error, String message, int status) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .timestamp(Instant.now())
                .build();
    }
}