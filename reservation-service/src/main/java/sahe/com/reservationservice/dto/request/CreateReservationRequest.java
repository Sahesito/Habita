package sahe.com.reservationservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateReservationRequest {

    @NotBlank(message = "Area id is required")
    private String areaId;

    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Positive(message = "Attendees count must be positive")
    private Integer attendeesCount;

    private String notes;
}