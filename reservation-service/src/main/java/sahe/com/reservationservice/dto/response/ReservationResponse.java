package sahe.com.reservationservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ReservationResponse {

    private String id;
    private AreaInfo area;
    private ResidentInfo resident;

    private LocalDate date;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("attendees_count")
    private Integer attendeesCount;

    private String notes;
    private String status;

    @JsonProperty("created_at")
    private Instant createdAt;

    @Data
    @Builder
    public static class AreaInfo {
        private String id;
        private String name;
        private Integer capacity;
    }

    @Data
    @Builder
    public static class ResidentInfo {
        private String id;
        private String fullName;
        private String apartment;
    }
}