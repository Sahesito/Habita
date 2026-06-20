package sahe.com.userservice.repository;

import sahe.com.userservice.domain.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {

    List<Apartment> findByTenantId(String tenantId);

    Optional<Apartment> findByTenantIdAndNumberAndTower(
            String tenantId, String number, String tower
    );
}