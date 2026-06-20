package sahe.com.reservationservice.dto.request;

import lombok.Data;

@Data
public class CancelReservationRequest {
    private String reason;
}