package sahe.com.authservice.domain.enums;

public enum RevokedReason {
    LOGOUT,
    PASSWORD_CHANGED,
    ADMIN_REVOKED,
    SUSPICIOUS_ACTIVITY,
    TOKEN_ROTATION
}