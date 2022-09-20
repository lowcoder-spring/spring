package icu.lowcoder.spring.cloud.feign;

import feign.Feign;
import icu.lowcoder.spring.cloud.feign.interceptor.BearerTokenRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Feign.class)
public class FeignAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "icu.lowcoder.spring.cloud.feign", name = "bearer-token-relay", value = "true")
    public BearerTokenRequestInterceptor bearerTokenRequestInterceptor() {
        return new BearerTokenRequestInterceptor();
    }

}
