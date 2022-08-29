package icu.lowcoder.spring.commons.exception.oauth2;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.NoSuchClientException;

public class NoSuchClientExceptionConverter implements UnifiedExceptionConverter<NoSuchClientException> {
    @Override
    public UnifiedExceptionResponse convert(NoSuchClientException e) {
        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("不允许的客户端")
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return NoSuchClientException.class.isAssignableFrom(clazz);
    }
}
