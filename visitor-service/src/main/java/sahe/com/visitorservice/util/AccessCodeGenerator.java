package sahe.com.visitorservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccessCodeGenerator {

    private static final String CHARS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {
        StringBuilder code = new StringBuilder("HAB-");
        for (int i = 0; i < 4; i++) {
            code.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return code.toString();
    }
}