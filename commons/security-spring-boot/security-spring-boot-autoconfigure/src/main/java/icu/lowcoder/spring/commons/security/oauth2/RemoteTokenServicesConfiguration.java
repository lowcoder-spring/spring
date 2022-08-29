package icu.lowcoder.spring.commons.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ResourceServerConfigurer.class)
@ConditionalOnProperty(prefix = "security.oauth2.resource", name = "token-info-uri")
public class RemoteTokenServicesConfiguration {

    @Bean("customizedRemoteTokenServices")
    @Primary
    public RemoteTokenServices remoteTokenServices(@Autowired(required = false) PrincipalExtractor principalExtractor,
                                                   @Autowired(required = false) AuthoritiesExtractor authoritiesExtractor,
                                                   ResourceServerProperties resourceServerProperties) {
        AccountAuthenticationConverter authenticationConverter = new AccountAuthenticationConverter();
        if (principalExtractor != null) {
            authenticationConverter.setPrincipalExtractor(principalExtractor);
        }
        if (authoritiesExtractor != null) {
            authenticationConverter.setAuthoritiesExtractor(authoritiesExtractor);
        }

        CustomizedAccessTokenConverter accessTokenConverter = new CustomizedAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(authenticationConverter);

        CustomizedRemoteTokenServices remoteTokenServices = new CustomizedRemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(resourceServerProperties.getTokenInfoUri());
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);

        return remoteTokenServices;
    }
}
