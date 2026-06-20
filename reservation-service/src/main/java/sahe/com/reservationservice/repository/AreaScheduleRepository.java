package sahe.com.reservationservice.repository;

import sahe.com.reservationservice.domain.entity.AreaSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AreaScheduleRepository extends JpaRepository<AreaSchedule, UUID> {

    List<AreaSchedule> findByAreaId(UUID areaId);

    Optional<AreaSchedule> findByAreaIdAndDayOfWeek(UUID areaId, Integer dayOfWeek);
}