package sahe.com.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sahe.com.userservice.domain.entity.Apartment;
import sahe.com.userservice.domain.entity.EmergencyContact;
import sahe.com.userservice.domain.entity.User;
import sahe.com.userservice.domain.entity.UserApartment;
import sahe.com.userservice.dto.request.CreateUserRequest;
import sahe.com.userservice.dto.response.UserResponse;
import sahe.com.userservice.exception.HabitaException;
import sahe.com.userservice.repository.ApartmentRepository;
import sahe.com.userservice.repository.UserApartmentRepository;
import sahe.com.userservice.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserApartmentRepository userApartmentRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request, String tenantId, String keycloakId) {
        log.info("Creating user: {} for tenant: {}", request.getEmail(), tenantId);

        if (userRepository.existsByTenantIdAndEmail(tenantId, request.getEmail())) {
            throw HabitaException.conflict("A user with this email already exists");
        }

        User user = User.builder()
                .keycloakId(keycloakId)
                .tenantId(tenantId)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status("ACTIVE")
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .build();

        user = userRepository.save(user);

        // Link apartments
        for (String apartmentIdStr : request.getApartmentIds()) {
            UUID apartmentId = UUID.fromString(apartmentIdStr);
            Apartment apartment = apartmentRepository.findById(apartmentId)
                    .orElseThrow(() -> HabitaException.notFound("Apartment not found: " + apartmentIdStr));

            UserApartment userApartment = UserApartment.builder()
                    .user(user)
                    .apartment(apartment)
                    .isOwner(true)
                    .build();

            UserApartment saved = userApartmentRepository.save(userApartment);
            user.getApartments().add(saved);
        }

        // Add emergency contact if provided
        if (request.getEmergencyContact() != null) {
            EmergencyContact contact = EmergencyContact.builder()
                    .user(user)
                    .name(request.getEmergencyContact().getName())
                    .phone(request.getEmergencyContact().getPhone())
                    .relationship(request.getEmergencyContact().getRelationship())
                    .build();
            user.getEmergencyContacts().add(contact);
        }

        log.info("User created successfully: {}", user.getId());

        return mapToResponse(user);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> HabitaException.notFound("User not found: " + id));
        return mapToResponse(user);
    }

    public UserResponse getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> HabitaException.notFound("User not found"));
        return mapToResponse(user);
    }

    public Page<UserResponse> getUsers(String tenantId, String role, Pageable pageable) {
        return userRepository.findByTenantAndRole(tenantId, role, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void updateStatus(UUID id, String status, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> HabitaException.notFound("User not found: " + id));

        user.setStatus(status);
        userRepository.save(user);

        log.info("User {} status updated to {} - reason: {}", id, status, reason);
    }

    @Transactional
    public void updateLastSeen(String keycloakId) {
        userRepository.findByKeycloakId(keycloakId).ifPresent(user -> {
            user.setLastSeenAt(java.time.Instant.now());
            userRepository.save(user);
        });
    }

    private UserResponse mapToResponse(User user) {
        List<UserResponse.ApartmentInfo> apartments = user.getApartments().stream()
                .map(ua -> UserResponse.ApartmentInfo.builder()
                        .id(ua.getApartment().getId().toString())
                        .number(ua.getApartment().getNumber())
                        .tower(ua.getApartment().getTower())
                        .floor(ua.getApartment().getFloor())
                        .build())
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .apartments(apartments)
                .build();
    }
}