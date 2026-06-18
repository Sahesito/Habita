package sahe.com.authservice.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sahe.com.authservice.exception.HabitaException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${habita.keycloak.url}")
    private String keycloakUrl;

    @Value("${habita.keycloak.realm}")
    private String realm;

    @Value("${habita.keycloak.client-id}")
    private String clientId;

    @Value("${habita.keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public KeycloakUser authenticate(String email, String password, String tenantId) {
        log.info("Authenticating user: {} against Keycloak realm: {}", email, realm);

        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", email);
        body.add("password", password);
        body.add("scope", "openid profile email");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            log.info("Calling Keycloak URL: {}", tokenUrl);
            log.info("client_id: {} username: {}", clientId, email);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            log.info("Keycloak response status: {}", response.getStatusCode());

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw HabitaException.unauthorized("Invalid credentials");
            }

            String accessToken = (String) response.getBody().get("access_token");
            return getUserInfoFromToken(accessToken, tenantId);

        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Authentication failed for user: {} - {}", email, e.getResponseBodyAsString());
            throw HabitaException.unauthorized("Invalid email or password");
        } catch (HttpClientErrorException e) {
            log.error("Keycloak HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw HabitaException.badRequest("Authentication service error");
        } catch (Exception e) {
            log.error("Unexpected error calling Keycloak: {}", e.getMessage(), e);
            throw HabitaException.unauthorized("Invalid email or password");
        }
    }

    public KeycloakUser getUserById(String userId, String tenantId) {
        log.info("Fetching user: {} from Keycloak", userId);

        String adminToken = getAdminToken();
        String userUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    userUrl, HttpMethod.GET, request, Map.class
            );

            if (response.getBody() == null) {
                throw HabitaException.notFound("User not found");
            }

            Map<String, Object> data = response.getBody();

            String firstName = (String) data.getOrDefault("firstName", "");
            String lastName  = (String) data.getOrDefault("lastName", "");
            String fullName  = (firstName + " " + lastName).trim();
            String email     = (String) data.get("email");
            String id        = (String) data.get("id");

            return KeycloakUser.builder()
                    .id(id)
                    .email(email)
                    .fullName(fullName.isEmpty() ? "Unknown" : fullName)
                    .role("RESIDENT")
                    .tenantId(tenantId)
                    .build();

        } catch (HttpClientErrorException.NotFound e) {
            throw HabitaException.notFound("User not found: " + userId);
        }
    }

    private KeycloakUser getUserInfoFromToken(String accessToken, String tenantId) {
        String userInfoUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, request, Map.class
        );

        if (response.getBody() == null) {
            throw HabitaException.unauthorized("Could not retrieve user info");
        }

        return mapToKeycloakUser(response.getBody(), tenantId);
    }

    private String getAdminToken() {
        String tokenUrl = keycloakUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", "admin");
        body.add("password", "admin123");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Could not obtain admin token from Keycloak");
        }

        return (String) response.getBody().get("access_token");
    }

    @SuppressWarnings("unchecked")
    private KeycloakUser mapToKeycloakUser(Map<String, Object> data, String tenantId) {
        String firstName = (String) data.getOrDefault("given_name", "");
        String lastName = (String) data.getOrDefault("family_name", "");
        String fullName = (firstName + " " + lastName).trim();

        if (fullName.isEmpty()) {
            fullName = (String) data.getOrDefault("name", "Unknown");
        }

        // Extract role from attributes or realm_access
        String role = "RESIDENT";
        Map<String, Object> realmAccess = (Map<String, Object>) data.get("realm_access");
        if (realmAccess != null) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles != null) {
                if (roles.contains("ADMIN")) role = "ADMIN";
                else if (roles.contains("SECURITY")) role = "SECURITY";
                else if (roles.contains("RESIDENT")) role = "RESIDENT";
            }
        }

        return KeycloakUser.builder()
                .id((String) data.get("sub"))
                .email((String) data.get("email"))
                .fullName(fullName)
                .role(role)
                .tenantId(tenantId)
                .build();
    }

    @Data
    @Builder
    public static class KeycloakUser {
        private String id;
        private String email;
        private String fullName;
        private String role;
        private String tenantId;
    }


}