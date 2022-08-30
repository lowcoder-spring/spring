package icu.lowcoder.spring.commons.exception.security;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationExceptionConverter implements UnifiedExceptionConverter<AuthenticationException> {

    @Override
    public UnifiedExceptionResponse convert(AuthenticationException e) {
        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("未认证")
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return AuthenticationException.class.isAssignableFrom(clazz);
    }
}
