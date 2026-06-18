package sahe.com.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sahe.com.authservice.domain.entity.AuthSession;
import sahe.com.authservice.domain.entity.RefreshToken;
import sahe.com.authservice.dto.request.LoginRequest;
import sahe.com.authservice.dto.request.RefreshTokenRequest;
import sahe.com.authservice.dto.response.AuthResponse;
import sahe.com.authservice.exception.InvalidTokenException;
import sahe.com.authservice.repository.AuthSessionRepository;
import sahe.com.authservice.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthSessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KeycloakService keycloakService;
    private final TokenService tokenService;

    @Value("${habita.jwt.expiration}")
    private long jwtExpiration;

    @Value("${habita.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.info("Login attempt for email: {} tenant: {}", request.getEmail(), request.getTenantId());

        // Authenticate against Keycloak
        KeycloakService.KeycloakUser kcUser = keycloakService.authenticate(
                request.getEmail(),
                request.getPassword(),
                request.getTenantId()
        );

        // Create session
        AuthSession session = AuthSession.builder()
                .userId(kcUser.getId())
                .tenantId(request.getTenantId())
                .jti(UUID.randomUUID().toString())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtExpiration))
                .build();

        sessionRepository.save(session);

        // Generate tokens
        String accessToken = tokenService.generateAccessToken(session, kcUser);
        String rawRefreshToken = tokenService.generateRefreshToken();
        String hashedRefreshToken = tokenService.hashToken(rawRefreshToken);

        // Save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .session(session)
                .tokenHash(hashedRefreshToken)
                .expiresAt(Instant.now().plusSeconds(refreshExpiration))
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("Login successful for user: {} tenant: {}", kcUser.getId(), request.getTenantId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .expiresIn(jwtExpiration)
                .user(AuthResponse.UserInfo.builder()
                        .id(kcUser.getId())
                        .email(kcUser.getEmail())
                        .fullName(kcUser.getFullName())
                        .role(kcUser.getRole())
                        .tenantId(request.getTenantId())
                        .build())
                .build();
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.info("Token refresh attempt");

        String tokenHash = tokenService.hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::invalid);

        if (!refreshToken.isValid()) {
            throw InvalidTokenException.expired();
        }

        AuthSession session = refreshToken.getSession();

        if (!session.isValid()) {
            throw InvalidTokenException.revoked();
        }

        // Rotate refresh token
        refreshToken.setUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        String newRawRefreshToken = tokenService.generateRefreshToken();
        String newHashedRefreshToken = tokenService.hashToken(newRawRefreshToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .session(session)
                .tokenHash(newHashedRefreshToken)
                .expiresAt(Instant.now().plusSeconds(refreshExpiration))
                .build();

        refreshTokenRepository.save(newRefreshToken);

        // Generate new access token
        KeycloakService.KeycloakUser kcUser = keycloakService.getUserById(
                session.getUserId(),
                session.getTenantId()
        );

        String newAccessToken = tokenService.generateAccessToken(session, kcUser);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRawRefreshToken)
                .expiresIn(jwtExpiration)
                .user(AuthResponse.UserInfo.builder()
                        .id(kcUser.getId())
                        .email(kcUser.getEmail())
                        .fullName(kcUser.getFullName())
                        .role(kcUser.getRole())
                        .tenantId(session.getTenantId())
                        .build())
                .build();
    }

    @Transactional
    public void logout(String jti) {
        log.info("Logout for jti: {}", jti);

        AuthSession session = sessionRepository.findByJti(jti)
                .orElseThrow(() -> InvalidTokenException.invalid());

        session.setRevokedAt(Instant.now());
        session.setRevokedReason("LOGOUT");
        sessionRepository.save(session);
    }
}