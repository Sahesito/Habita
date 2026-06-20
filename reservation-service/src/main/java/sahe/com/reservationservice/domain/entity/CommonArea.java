package sahe.com.reservationservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "common_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonArea {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String rules;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "advance_days", nullable = false)
    @Builder.Default
    private Integer advanceDays = 30;

    @Column(name = "min_hours", precision = 4, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal minHours = BigDecimal.valueOf(1.0);

    @Column(name = "max_hours", precision = 4, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal maxHours = BigDecimal.valueOf(4.0);

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}