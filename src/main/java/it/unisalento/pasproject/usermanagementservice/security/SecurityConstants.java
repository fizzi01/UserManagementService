package it.unisalento.pasproject.usermanagementservice.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The SecurityConstants class provides constants related to security configuration.
 * It includes a JWT_SECRET constant that is used for signing JSON Web Tokens (JWTs).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Getter
public class SecurityConstants {

    private SecurityConstants() {}

    @Value("${secret.key}")
    public String JWT_SECRET;

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_UTENTE = "UTENTE";
    public static final String ROLE_MEMBRO = "MEMBRO";
}
