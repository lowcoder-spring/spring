package icu.lowcoder.spring.commons.security;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Configuration(proxyBeanMethods = false)
public class MethodSecurityConfiguration {

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @ConditionalOnClass(value = {OAuth2MethodSecurityExpressionHandler.class, GlobalMethodSecurityConfiguration.class})
    static class OAuth2MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            return new OAuth2MethodSecurityExpressionHandler();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @ConditionalOnClass(value = {GlobalMethodSecurityConfiguration.class})
    @ConditionalOnMissingClass("org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler")
    @AutoConfigureAfter(OAuth2MethodSecurityConfiguration.class)
    static class DefaultMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    }
}
