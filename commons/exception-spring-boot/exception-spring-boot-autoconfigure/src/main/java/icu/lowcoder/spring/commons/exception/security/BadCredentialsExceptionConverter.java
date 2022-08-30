package icu.lowcoder.spring.commons.exception.security;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

public class BadCredentialsExceptionConverter implements UnifiedExceptionConverter<BadCredentialsException> {

    @Override
    public UnifiedExceptionResponse convert(BadCredentialsException e) {
        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return BadCredentialsException.class.isAssignableFrom(clazz);
    }
}
