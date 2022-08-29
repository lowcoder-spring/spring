package icu.lowcoder.spring.commons.exception.feign;

import icu.lowcoder.spring.commons.exception.ExceptionConvertersConfigurerAdapter;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import feign.FeignException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass(FeignException.class)
public class FeignExceptionHandlerAutoConfiguration {

    @Configuration
    static class FeignExceptionConvertersConfiguration extends ExceptionConvertersConfigurerAdapter {
        @Override
        public void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception {
            List<UnifiedExceptionConverter<? extends Exception>> exceptionConverters = new ArrayList<>();
            exceptionConverters.add(new FeignExceptionConverter());

            converters.addAll(0, exceptionConverters);
        }
    }
}
