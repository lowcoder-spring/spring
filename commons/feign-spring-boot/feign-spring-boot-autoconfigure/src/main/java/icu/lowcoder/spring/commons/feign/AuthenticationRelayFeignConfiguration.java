package icu.lowcoder.spring.commons.feign;

import icu.lowcoder.spring.commons.feign.interceptor.AuthenticationRelayRequestInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

@Deprecated
public class AuthenticationRelayFeignConfiguration {
    /*@Bean
    public RequestInterceptor authenticationRelayRequestInterceptor() {
        return new AuthenticationRelayRequestInterceptor();
    }*/

    /*@Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }*/
}
