package sahe.com.userservice.repository;

import sahe.com.userservice.domain.entity.UserApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserApartmentRepository extends JpaRepository<UserApartment, UUID> {

    List<UserApartment> findByUserId(UUID userId);

    List<UserApartment> findByApartmentId(UUID apartmentId);
}