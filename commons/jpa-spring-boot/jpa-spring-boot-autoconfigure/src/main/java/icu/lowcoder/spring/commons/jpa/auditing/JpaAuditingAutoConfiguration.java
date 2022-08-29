package icu.lowcoder.spring.commons.jpa.auditing;

import icu.lowcoder.spring.commons.jpa.JpaAuditingProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@Configuration
@ConditionalOnClass({AuditorAware.class, Authentication.class})
@EnableConfigurationProperties(JpaAuditingProperties.class)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.jpa.auditing", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaAuditing
public class JpaAuditingAutoConfiguration {

    @Bean
    public AuditorAware<String> auditorAware(JpaAuditingProperties jpaAuditingProperties) {
        return () -> {
            AuthenticationAuditorExtractor authenticationAuditorExtractor = new AuthenticationAuditorExtractor(jpaAuditingProperties);
            Object auditor = authenticationAuditorExtractor.extractor();

            return Optional.ofNullable(auditor == null ? null : String.valueOf(auditor));
        };
    }

}
