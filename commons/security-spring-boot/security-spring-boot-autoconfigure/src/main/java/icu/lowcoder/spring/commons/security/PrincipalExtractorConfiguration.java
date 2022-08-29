package icu.lowcoder.spring.commons.security;

import icu.lowcoder.spring.commons.security.oauth2.AccountPrincipalExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// todo 适配oauth2
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PrincipalExtractor.class)
public class PrincipalExtractorConfiguration {

    @Bean
    @ConditionalOnMissingBean(PrincipalExtractor.class)
    public PrincipalExtractor defaultPrincipalExtractor() {
        return new AccountPrincipalExtractor();
    }

}
