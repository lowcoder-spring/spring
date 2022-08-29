package icu.lowcoder.spring.commons.exception.security;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class AuthenticationCredentialsNotFoundExceptionConverter implements UnifiedExceptionConverter<AuthenticationCredentialsNotFoundException> {
    @Override
    public UnifiedExceptionResponse convert(AuthenticationCredentialsNotFoundException e) {
        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("未认证")
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return AuthenticationCredentialsNotFoundException.class.isAssignableFrom(clazz);
    }
}
