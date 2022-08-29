package icu.lowcoder.spring.commons.exception.converter;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;

public class DefaultExceptionConverter implements UnifiedExceptionConverter<Exception> {

    @Override
    public UnifiedExceptionResponse convert(Exception e) {
        return UnifiedExceptionResponse.builder()
                .status(500)
                .exception(e.getClass().getName())
                .message("Internal Server Error")
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return true;
    }
}
