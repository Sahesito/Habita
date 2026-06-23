package sahe.com.visitorservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "visitors", indexes = {
        @Index(name = "idx_visitors_tenant", columnList = "tenant_id"),
        @Index(name = "idx_visitors_host",   columnList = "tenant_id, host_resident_id"),
        @Index(name = "idx_visitors_date",   columnList = "tenant_id, expected_date"),
        @Index(name = "idx_visitors_status", columnList = "tenant_id, status"),
        @Index(name = "idx_visitors_code",   columnList = "access_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(name = "visitor_name", nullable = false)
    private String visitorName;

    @Column(name = "visitor_document", length = 50)
    private String visitorDocument;

    @Column(name = "visitor_phone", length = 20)
    private String visitorPhone;

    @Column(name = "visitor_photo_url")
    private String visitorPhotoUrl;

    @Column(name = "host_resident_id", nullable = false)
    private String hostResidentId;

    @Column(name = "host_email")
    private String hostEmail;

    @Column(name = "host_phone")
    private String hostPhone;

    @Column(name = "apartment_label")
    private String apartmentLabel;

    @Column(name = "access_code", nullable = false, unique = true, length = 20)
    private String accessCode;

    @Column(name = "qr_token", length = 500)
    private String qrToken;

    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Column(name = "expected_time_from")
    private LocalTime expectedTimeFrom;

    @Column(name = "expected_time_to")
    private LocalTime expectedTimeTo;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String purpose = "VISIT";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "EXPECTED";

    @Column(name = "checkin_at")
    private Instant checkinAt;

    @Column(name = "checkin_guard_id")
    private String checkinGuardId;

    @Column(name = "checkout_at")
    private Instant checkoutAt;

    @Column(name = "checkout_guard_id")
    private String checkoutGuardId;

    @Column(name = "vehicle_plate", length = 20)
    private String vehiclePlate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isInside() {
        return "INSIDE".equals(status);
    }
}