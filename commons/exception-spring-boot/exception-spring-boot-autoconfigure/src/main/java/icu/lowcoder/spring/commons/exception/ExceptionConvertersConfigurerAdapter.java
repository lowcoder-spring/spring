package icu.lowcoder.spring.commons.exception;

import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;

import java.util.List;

public class ExceptionConvertersConfigurerAdapter implements ExceptionConvertersConfigurer {
    @Override
    public void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception {

    }
}
