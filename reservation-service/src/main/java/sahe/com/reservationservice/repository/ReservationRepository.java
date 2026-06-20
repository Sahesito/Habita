package sahe.com.reservationservice.repository;

import sahe.com.reservationservice.domain.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Page<Reservation> findByTenantIdAndResidentId(String tenantId, String residentId, Pageable pageable);

    Page<Reservation> findByTenantId(String tenantId, Pageable pageable);

    @Query("""
            SELECT COUNT(r) FROM Reservation r
            WHERE r.area.id = :areaId
              AND r.date = :date
              AND r.status = 'CONFIRMED'
              AND r.startTime < :endTime
              AND r.endTime > :startTime
            """)
    long countConflicts(
            @Param("areaId") UUID areaId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.area.id = :areaId
              AND r.date = :date
              AND r.status = 'CONFIRMED'
            ORDER BY r.startTime
            """)
    List<Reservation> findConfirmedByAreaAndDate(
            @Param("areaId") UUID areaId,
            @Param("date") LocalDate date
    );
}