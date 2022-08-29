package icu.lowcoder.spring.commons.exception;

import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;

import java.util.List;

public interface ExceptionConvertersConfigurer {
    void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception;
}
