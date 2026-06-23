package sahe.com.visitorservice.repository;

import sahe.com.visitorservice.domain.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, UUID> {

    Optional<Visitor> findByAccessCode(String accessCode);

    boolean existsByAccessCode(String accessCode);

    Page<Visitor> findByTenantIdAndHostResidentId(String tenantId, String hostResidentId, Pageable pageable);

    List<Visitor> findByTenantIdAndStatus(String tenantId, String status);

    @Query("""
            SELECT v FROM Visitor v
            WHERE v.tenantId = :tenantId
              AND v.createdAt BETWEEN :from AND :to
            ORDER BY v.createdAt DESC
            """)
    Page<Visitor> findHistory(
            @Param("tenantId") String tenantId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );
}