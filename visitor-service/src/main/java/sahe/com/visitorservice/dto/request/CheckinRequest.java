package sahe.com.visitorservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckinRequest {

    @NotBlank(message = "Access code is required")
    private String accessCode;

    private String vehiclePlate;
}