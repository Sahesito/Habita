package sahe.com.reservationservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservations_slot", columnList = "area_id, date, start_time, end_time"),
        @Index(name = "idx_reservations_resident", columnList = "tenant_id, resident_id"),
        @Index(name = "idx_reservations_date", columnList = "tenant_id, date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private CommonArea area;

    @Column(name = "resident_id", nullable = false)
    private String residentId;

    @Column(name = "resident_email")
    private String residentEmail;

    @Column(name = "resident_name")
    private String residentName;

    @Column(name = "apartment_label")
    private String apartmentLabel;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "attendees_count")
    private Integer attendeesCount;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "CONFIRMED";

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

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
}