package sahe.com.authservice.repository;

import sahe.com.authservice.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken r
            WHERE r.expiresAt < :threshold
            """)
    int deleteExpiredTokens(@Param("threshold") Instant threshold);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken r
            WHERE r.session.id = :sessionId
            """)
    int deleteBySessionId(@Param("sessionId") UUID sessionId);
}