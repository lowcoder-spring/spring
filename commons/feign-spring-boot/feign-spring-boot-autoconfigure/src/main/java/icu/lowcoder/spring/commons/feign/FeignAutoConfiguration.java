package icu.lowcoder.spring.commons.feign;

import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Configuration
@ConditionalOnClass(Feign.class)
@EnableConfigurationProperties({FeignClientProperties.class, FeignHttpClientProperties.class})
public class FeignAutoConfiguration {

    /**
     * service authenticate use client_credentials
     * @return
     */
    @Bean(name = "serviceAuthenticateOAuth2ProtectedResourceDetails")
    @ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.feign.service-authenticate")
    public OAuth2ProtectedResourceDetails serviceAuthenticateOAuth2ProtectedResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

}
