package icu.lowcoder.spring.commons.exception.oauth2;

import icu.lowcoder.spring.commons.exception.ExceptionConvertersConfigurerAdapter;
import icu.lowcoder.spring.commons.exception.ExceptionResponseBuilder;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import java.util.ArrayList;
import java.util.List;

// TODO 适配新版本 oauth2
@Configuration
@ConditionalOnClass(OAuth2Exception.class)
@Import({AuthorizationServerConfiguration.class, ResourceServerConfiguration.class})
public class OAuth2ExceptionHandlerAutoConfiguration {

    @Bean
    @SuppressWarnings({"rawtypes"})
    public WebResponseExceptionTranslator oAuth2ExceptionWebResponseExceptionTranslator(ExceptionResponseBuilder exceptionResponseBuilder) {
        return new OAuth2WebResponseExceptionTranslator(exceptionResponseBuilder);
    }

    @Configuration
    static class OAuth2ExceptionConvertersConfiguration extends ExceptionConvertersConfigurerAdapter {
        @Override
        public void configure(List<UnifiedExceptionConverter<? extends Exception>> converters) throws Exception {
            List<UnifiedExceptionConverter<? extends Exception>> exceptionConverters = new ArrayList<>();
            exceptionConverters.add(new NoSuchClientExceptionConverter());
            exceptionConverters.add(new OAuth2ExceptionConverter());

            converters.addAll(0, exceptionConverters);
        }
    }

}
