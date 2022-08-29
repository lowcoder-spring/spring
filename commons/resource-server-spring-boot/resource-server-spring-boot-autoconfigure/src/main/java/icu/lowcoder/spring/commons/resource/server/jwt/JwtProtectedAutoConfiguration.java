package icu.lowcoder.spring.commons.resource.server.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProtectedProperties.class)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.resource.server.jwt", name = "public-key")
public class JwtProtectedAutoConfiguration {

    @Bean
    public JwtDecoder jwtDecoder(JwtProtectedProperties jwtProtectedProperties) {
        return NimbusJwtDecoder.withPublicKey(jwtProtectedProperties.getPublicKey()).build();
    }
}
