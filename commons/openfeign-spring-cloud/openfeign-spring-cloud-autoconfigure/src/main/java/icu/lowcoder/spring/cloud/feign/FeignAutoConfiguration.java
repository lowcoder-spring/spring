package icu.lowcoder.spring.cloud.feign;

import feign.Feign;
import icu.lowcoder.spring.cloud.feign.interceptor.BearerTokenRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.security.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Feign.class)
public class FeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OAuth2FeignRequestInterceptor.class)
    public BearerTokenRequestInterceptor bearerTokenRequestInterceptor() {
        return new BearerTokenRequestInterceptor();
    }

}
