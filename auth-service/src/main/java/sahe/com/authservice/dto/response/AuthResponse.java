package sahe.com.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private long expiresIn;

    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private String id;
        private String email;

        @JsonProperty("full_name")
        private String fullName;

        private String role;

        @JsonProperty("tenant_id")
        private String tenantId;
    }
}