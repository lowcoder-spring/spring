package icu.lowcoder.spring.commons.exception.security;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

public class AccessDeniedExceptionConverter implements UnifiedExceptionConverter<AccessDeniedException> {
    @Override
    public UnifiedExceptionResponse convert(AccessDeniedException e) {
        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage())
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return AccessDeniedException.class.isAssignableFrom(clazz);
    }
}
