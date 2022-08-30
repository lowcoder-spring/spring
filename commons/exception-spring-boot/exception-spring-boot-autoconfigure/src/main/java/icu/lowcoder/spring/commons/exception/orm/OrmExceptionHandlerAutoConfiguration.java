package icu.lowcoder.spring.commons.exception.orm;

import icu.lowcoder.spring.commons.exception.ExceptionConvertersConfigurerAdapter;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass(ObjectOptimisticLockingFailureException.class)
public class OrmExceptionHandlerAutoConfiguration {

    @Configuration
    static class OrmExceptionConvertersConfiguration extends ExceptionConvertersConfigurerAdapter {
        @Override
        public void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception {
            List<UnifiedExceptionConverter<? extends Exception>> exceptionConverters = new ArrayList<>();
            exceptionConverters.add(new ObjectOptimisticLockingFailureExceptionConverter());

            converters.addAll(0, exceptionConverters);
        }
    }

}
