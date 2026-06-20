package sahe.com.reservationservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "area_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"area_id", "day_of_week"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private CommonArea area;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 0=Monday, 6=Sunday

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;
}