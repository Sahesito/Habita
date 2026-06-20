package sahe.com.userservice.repository;

import sahe.com.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByTenantIdAndEmail(String tenantId, String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.tenantId = :tenantId
              AND (:role IS NULL OR u.role = :role)
              AND u.deletedAt IS NULL
            """)
    org.springframework.data.domain.Page<User> findByTenantAndRole(
            @Param("tenantId") String tenantId,
            @Param("role") String role,
            org.springframework.data.domain.Pageable pageable
    );

    boolean existsByTenantIdAndEmail(String tenantId, String email);
}