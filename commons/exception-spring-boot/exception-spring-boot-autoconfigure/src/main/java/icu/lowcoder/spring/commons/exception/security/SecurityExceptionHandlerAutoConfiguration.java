package icu.lowcoder.spring.commons.exception.security;

import icu.lowcoder.spring.commons.exception.ExceptionConvertersConfigurerAdapter;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass(Authentication.class)
public class SecurityExceptionHandlerAutoConfiguration {

    @Configuration
    static class SecurityExceptionConvertersConfiguration extends ExceptionConvertersConfigurerAdapter {
        @Override
        public void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception {
            List<UnifiedExceptionConverter<? extends Exception>> exceptionConverters = new ArrayList<>();
            exceptionConverters.add(new AccessDeniedExceptionConverter());
            exceptionConverters.add(new AuthenticationCredentialsNotFoundExceptionConverter());
            exceptionConverters.add(new UsernameNotFoundExceptionConverter());
            exceptionConverters.add(new BadCredentialsExceptionConverter());
            exceptionConverters.add(new InsufficientAuthenticationExceptionConverter());
            exceptionConverters.add(new AuthenticationExceptionConverter());

            converters.addAll(0, exceptionConverters);
        }
    }

}
