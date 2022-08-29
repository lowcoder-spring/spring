package icu.lowcoder.spring.commons.exception.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

@Configuration
@ConditionalOnClass(ResourceServerConfigurer.class)
public class ResourceServerConfiguration implements ResourceServerConfigurer {
    @SuppressWarnings({"rawtypes"})
    private final WebResponseExceptionTranslator exceptionTranslator;

    @SuppressWarnings({"rawtypes"})
    public ResourceServerConfiguration(@Autowired(required = false) WebResponseExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        if (exceptionTranslator != null) { // OAuth2AuthenticationEntryPoint
            resources.addObjectPostProcessor(new ObjectPostProcessor<OAuth2AuthenticationEntryPoint>() {
                @Override
                public <O extends OAuth2AuthenticationEntryPoint> O postProcess(O object) {
                    object.setExceptionTranslator(exceptionTranslator);
                    return object;
                }
            });

            OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler = new OAuth2AccessDeniedHandler();
            oAuth2AccessDeniedHandler.setExceptionTranslator(exceptionTranslator);
            resources.accessDeniedHandler(oAuth2AccessDeniedHandler);
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

    }

}
