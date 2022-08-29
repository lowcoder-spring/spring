package icu.lowcoder.spring.commons.security;

import icu.lowcoder.spring.commons.security.oauth2.RemoteTokenServicesConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecurityProperties.class)
@Import({PrincipalExtractorConfiguration.class, RemoteTokenServicesConfiguration.class, MethodSecurityConfiguration.class })
public class SecurityAutoConfiguration {
}
