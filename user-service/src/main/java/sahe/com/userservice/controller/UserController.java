package sahe.com.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sahe.com.userservice.dto.request.CreateUserRequest;
import sahe.com.userservice.dto.request.UpdateStatusRequest;
import sahe.com.userservice.dto.response.UserResponse;
import sahe.com.userservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader(value = "X-Keycloak-Id", required = false) String keycloakId) {

        String kcId = keycloakId != null ? keycloakId : UUID.randomUUID().toString();
        UserResponse response = userService.createUser(request, tenantId, kcId);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String role,
            Pageable pageable) {

        return ResponseEntity.ok(userService.getUsers(tenantId, role, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {

        userService.updateStatus(id, request.getStatus(), request.getReason());
        return ResponseEntity.noContent().build();
    }
}