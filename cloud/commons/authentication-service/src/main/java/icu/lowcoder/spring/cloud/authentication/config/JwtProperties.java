package icu.lowcoder.spring.cloud.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Getter
@Setter
@Configuration
@ConfigurationProperties("icu.lowcoder.spring.cloud.auth.jwt")
public class JwtProperties {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private String issuer;
    private String keyId = UUID.randomUUID().toString();
    private Long expirySeconds = 86400L; // one day
}
