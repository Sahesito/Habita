package sahe.com.reservationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateAreaRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    private String location;
    private String rules;
}