package icu.lowcoder.spring.commons.exception.web;

import icu.lowcoder.spring.commons.exception.ExceptionConvertersConfigurerAdapter;
import icu.lowcoder.spring.commons.exception.ExceptionResponseBuilder;
import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebExceptionHandlerAutoConfiguration {

    @Configuration
    static class WebExceptionConvertersConfiguration extends ExceptionConvertersConfigurerAdapter {
        @Override
        public void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception {
            List<UnifiedExceptionConverter<? extends Exception>> webExceptionConverters = new ArrayList<>();
            webExceptionConverters.add(new SpringWebExceptionConverter());

            converters.addAll(0, webExceptionConverters);
        }
    }

    @Configuration
    @ControllerAdvice
    static class GlobalControllerExceptionHandler {
        private final ExceptionResponseBuilder responseBuilder;

        public GlobalControllerExceptionHandler(ExceptionResponseBuilder responseBuilder) {
            this.responseBuilder = responseBuilder;
        }

        @ExceptionHandler(Exception.class)
        @ResponseBody
        public UnifiedExceptionResponse handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
            UnifiedExceptionResponse exceptionResponse;
            if (responseBuilder == null) {
                ExceptionResponseBuilder defaultBuilder = new ExceptionResponseBuilder();
                exceptionResponse = defaultBuilder.build(e, request.getRequestURI());
            } else {
                exceptionResponse = responseBuilder.build(e, request.getRequestURI());
            }

            // set http status
            response.setStatus(exceptionResponse.getStatus());
            return exceptionResponse;
        }

    }
}
