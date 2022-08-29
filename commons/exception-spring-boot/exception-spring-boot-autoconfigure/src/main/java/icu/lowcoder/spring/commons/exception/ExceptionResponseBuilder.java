package icu.lowcoder.spring.commons.exception;

import icu.lowcoder.spring.commons.exception.converter.DefaultExceptionConverter;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;

import java.util.ArrayList;
import java.util.List;

public class ExceptionResponseBuilder {

    private final List<UnifiedExceptionConverter<? extends Exception>> converters = new ArrayList<>();
    private ExceptionHandlerProperties exceptionHandlerProperties = new ExceptionHandlerProperties();

    public void setExceptionHandlerProperties(ExceptionHandlerProperties exceptionHandlerProperties) {
        this.exceptionHandlerProperties = exceptionHandlerProperties;
    }

    public ExceptionResponseBuilder() {
        this.converters.add(new DefaultExceptionConverter());
    }

    public ExceptionResponseBuilder(ExceptionHandlerProperties handlerProperties) {
        this.converters.add(new DefaultExceptionConverter());
        this.exceptionHandlerProperties = handlerProperties;
    }
    public ExceptionResponseBuilder(ExceptionHandlerProperties handlerProperties, List<UnifiedExceptionConverter<? extends Exception>> converters) {
        this.converters.addAll(converters);
        this.converters.add(new DefaultExceptionConverter());
        this.exceptionHandlerProperties = handlerProperties;
    }
    public void addConverter(UnifiedExceptionConverter<? extends Exception> converter) {
        this.converters.add(0, converter);
    }
    public void addConverters(List<UnifiedExceptionConverter<? extends Exception>> converters) {
        this.converters.addAll(0, converters);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public UnifiedExceptionResponse build(Throwable e, String uri) {
        for (UnifiedExceptionConverter converter : converters) {
            if (converter.support(e.getClass())) {
                UnifiedExceptionResponse response = converter.convert(e);
                response.setUri(uri);
                if (this.exceptionHandlerProperties.isShowDetail()) {
                    response.setDetail(e.getMessage());
                }
                if (this.exceptionHandlerProperties.isPrintStackTrace()) {
                    e.printStackTrace();
                }
                return response;
            }
        }

        UnifiedExceptionResponse res = UnifiedExceptionResponse.builder()
                .uri(uri)
                .status(500)
                .exception(e.getClass().getName())
                .message("未知错误")
                .build();
        if (this.exceptionHandlerProperties.isShowDetail()) {
            res.setDetail(e.getMessage());
        }
        if (this.exceptionHandlerProperties.isPrintStackTrace()) {
            e.printStackTrace();
        }

        return res;
    }
}
