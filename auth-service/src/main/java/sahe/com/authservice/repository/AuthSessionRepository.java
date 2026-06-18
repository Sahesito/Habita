package sahe.com.authservice.repository;

import sahe.com.authservice.domain.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {

    Optional<AuthSession> findByJti(String jti);

    List<AuthSession> findByUserIdAndTenantId(String userId, String tenantId);

    @Query("""
            SELECT s FROM AuthSession s
            WHERE s.userId = :userId
              AND s.tenantId = :tenantId
              AND s.revokedAt IS NULL
              AND s.expiresAt > :now
            """)
    List<AuthSession> findActiveSessionsByUser(
            @Param("userId") String userId,
            @Param("tenantId") String tenantId,
            @Param("now") Instant now
    );

    @Modifying
    @Query("""
            UPDATE AuthSession s
            SET s.revokedAt = :now,
                s.revokedReason = :reason
            WHERE s.userId = :userId
              AND s.tenantId = :tenantId
              AND s.revokedAt IS NULL
            """)
    int revokeAllUserSessions(
            @Param("userId") String userId,
            @Param("tenantId") String tenantId,
            @Param("now") Instant now,
            @Param("reason") String reason
    );

    @Modifying
    @Query("""
            DELETE FROM AuthSession s
            WHERE s.expiresAt < :threshold
            """)
    int deleteExpiredSessions(@Param("threshold") Instant threshold);
}