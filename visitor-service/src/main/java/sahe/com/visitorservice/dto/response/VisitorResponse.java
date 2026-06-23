package sahe.com.visitorservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class VisitorResponse {

    private String id;

    @JsonProperty("visitor_name")
    private String visitorName;

    private HostInfo host;

    @JsonProperty("access_code")
    private String accessCode;

    @JsonProperty("qr_url")
    private String qrUrl;

    @JsonProperty("expected_date")
    private LocalDate expectedDate;

    @JsonProperty("expected_time_from")
    private LocalTime expectedTimeFrom;

    @JsonProperty("expected_time_to")
    private LocalTime expectedTimeTo;

    private String status;

    @JsonProperty("checkin_at")
    private Instant checkinAt;

    @JsonProperty("checkout_at")
    private Instant checkoutAt;

    @JsonProperty("vehicle_plate")
    private String vehiclePlate;

    @JsonProperty("created_at")
    private Instant createdAt;

    @Data
    @Builder
    public static class HostInfo {
        private String id;

        @JsonProperty("full_name")
        private String fullName;

        private String apartment;
        private String phone;
    }
}