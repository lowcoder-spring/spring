package icu.lowcoder.spring.commons.resource.server.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
@Configuration
@ConfigurationProperties("icu.lowcoder.spring.commons.resource.server.jwt")
public class JwtProtectedProperties {
    private RSAPublicKey publicKey;
}
