package icu.lowcoder.spring.commons.feign;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import icu.lowcoder.spring.commons.feign.interceptor.ServiceAuthenticateRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.util.StringUtils;


// TODO 适配oauth2
public class ServiceAuthenticateFeignConfiguration {

    @Bean(name = "serviceAuthenticateOAuth2ClientContext")
    public OAuth2ClientContext serviceAuthenticateOAuth2ClientContext() {
        return new DefaultOAuth2ClientContext();
    }

    @Bean(name = "serviceAuthenticateOAuth2RestTemplate")
    public OAuth2RestTemplate serviceAuthenticateOAuth2RestTemplate(
            @Qualifier("serviceAuthenticateOAuth2ProtectedResourceDetails") OAuth2ProtectedResourceDetails resource,
            @Qualifier("serviceAuthenticateOAuth2ClientContext") OAuth2ClientContext context) {

        if (!StringUtils.hasText(resource.getAccessTokenUri())) {
            throw new RuntimeException("Feign service authenticate access token uri can't be empty.");
        }

        return new OAuth2RestTemplate(resource, context);
    }

    @Bean
    public RequestInterceptor oauth2ClientFeignRequestInterceptor(@Qualifier("serviceAuthenticateOAuth2RestTemplate") OAuth2RestTemplate oAuth2RestTemplate) {
        return new ServiceAuthenticateRequestInterceptor(oAuth2RestTemplate);
    }

    // error decoder
    @Bean
    public ErrorDecoder errorDecoder(@Qualifier("serviceAuthenticateOAuth2ClientContext") OAuth2ClientContext context) {
        return (key, response) -> {
            if (response.status() == HttpStatus.UNAUTHORIZED.value()) {
                // clear context access token
                context.setAccessToken(null);
            }
            return new ErrorDecoder.Default().decode(key, response);
        };
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
