package icu.lowcoder.spring.commons.exception.converter;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;

public interface UnifiedExceptionConverter<T> {
    UnifiedExceptionResponse convert(T t);

    boolean support(Class<?> clazz);
}
