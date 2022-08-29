package icu.lowcoder.spring.commons.exception;

import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(ExceptionHandlerProperties.class)
public class ExceptionHandlerAutoConfiguration {

    private final List<UnifiedExceptionConverter<? extends Exception>> converters = new ArrayList<>();

    private final List<ExceptionConvertersConfigurer> configurers;

    @Autowired
    public ExceptionHandlerAutoConfiguration(List<ExceptionConvertersConfigurer> configurers) {
        this.configurers = configurers;
    }

    @PostConstruct
    public void init() {
        for (ExceptionConvertersConfigurer configurer : configurers) {
            try {
                configurer.configure(converters);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot configure exception converters", e);
            }
        }
    }

    @Bean
    public ExceptionResponseBuilder exceptionResponseBuilder(ExceptionHandlerProperties exceptionHandlerProperties) {
        return new ExceptionResponseBuilder(exceptionHandlerProperties, converters);
    }

}
