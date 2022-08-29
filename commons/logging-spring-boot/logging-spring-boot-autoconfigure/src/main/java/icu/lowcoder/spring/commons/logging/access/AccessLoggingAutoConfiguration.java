package icu.lowcoder.spring.commons.logging.access;

import icu.lowcoder.spring.commons.logging.access.handler.AccessLogPrinter;
import icu.lowcoder.spring.commons.logging.access.handler.EmptyPrincipalExtractor;
import icu.lowcoder.spring.commons.logging.access.handler.SecurityContextPrincipalExtractor;
import icu.lowcoder.spring.commons.logging.access.handler.StringPrincipalExtractor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(HandlerInterceptor.class)
@EnableConfigurationProperties(AccessLoggingProperties.class)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.logging.access", name = "enabled", havingValue = "true")
public class AccessLoggingAutoConfiguration {

    @Bean
    @ConditionalOnClass(SecurityContextHolder.class)
    public StringPrincipalExtractor SecurityContextPrincipalExtractor() {
        return new SecurityContextPrincipalExtractor();
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.security.core.context.SecurityContextHolder")
    public StringPrincipalExtractor defaultPrincipalExtractor() {
        return new EmptyPrincipalExtractor();
    }

    @Bean
    public AccessLogPrinter defaultPrinter(AccessLoggingProperties accessLoggingProperties, StringPrincipalExtractor principalExtractor) {
        return new AccessLogPrinter(accessLoggingProperties, principalExtractor);
    }

    @Bean
    public FilterRegistrationBean<RequestAndResponseLoggingFilter> accessRequestLoggingFilterRegistration(AccessLoggingProperties accessLoggingProperties, AccessLogPrinter accessLogPrinter) {
        RequestAndResponseLoggingFilter accessRequestLoggingFilter = new RequestAndResponseLoggingFilter(accessLoggingProperties, accessLogPrinter);
        FilterRegistrationBean<RequestAndResponseLoggingFilter> registration = new FilterRegistrationBean<>(accessRequestLoggingFilter);
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        //registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return registration;
    }


}
