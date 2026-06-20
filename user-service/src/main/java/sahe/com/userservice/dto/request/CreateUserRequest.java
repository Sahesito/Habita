package sahe.com.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotBlank(message = "Role is required")
    private String role;

    @NotEmpty(message = "At least one apartment is required")
    private List<String> apartmentIds;

    private String documentType;
    private String documentNumber;

    private EmergencyContactRequest emergencyContact;

    @Data
    public static class EmergencyContactRequest {
        @NotBlank(message = "Emergency contact name is required")
        private String name;

        @NotBlank(message = "Emergency contact phone is required")
        private String phone;

        private String relationship;
    }
}