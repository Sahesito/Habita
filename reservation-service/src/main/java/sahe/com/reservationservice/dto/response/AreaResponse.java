package sahe.com.reservationservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AreaResponse {
    private String id;
    private String name;
    private String description;
    private Integer capacity;
    private String location;
    private String status;
}