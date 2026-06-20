package sahe.com.reservationservice.repository;

import sahe.com.reservationservice.domain.entity.CommonArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommonAreaRepository extends JpaRepository<CommonArea, UUID> {

    List<CommonArea> findByTenantIdAndStatus(String tenantId, String status);

    List<CommonArea> findByTenantId(String tenantId);
}