package sahe.com.visitorservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateVisitorRequest {

    @NotBlank(message = "Visitor name is required")
    private String visitorName;

    private String visitorDocument;
    private String visitorPhone;

    private LocalDate expectedDate;
    private LocalTime expectedTimeFrom;
    private LocalTime expectedTimeTo;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    private String notes;
}