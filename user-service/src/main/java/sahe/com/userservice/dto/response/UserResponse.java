package sahe.com.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class UserResponse {

    private String id;
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    private String phone;
    private String role;
    private String status;

    @JsonProperty("created_at")
    private Instant createdAt;

    private List<ApartmentInfo> apartments;

    @Data
    @Builder
    public static class ApartmentInfo {
        private String id;
        private String number;
        private String tower;
        private Integer floor;
    }
}