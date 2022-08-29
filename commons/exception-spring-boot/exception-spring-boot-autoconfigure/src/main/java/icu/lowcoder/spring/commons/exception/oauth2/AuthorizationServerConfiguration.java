package icu.lowcoder.spring.commons.exception.oauth2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AuthorizationServerEndpointsConfigurer.class)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @SuppressWarnings({"rawtypes"})
    private final WebResponseExceptionTranslator exceptionTranslator;

    @SuppressWarnings({"rawtypes"})
    public AuthorizationServerConfiguration(WebResponseExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.exceptionTranslator(exceptionTranslator);
    }

    @Configuration
    @AutoConfigureAfter({AuthorizationServerEndpointsConfiguration.class, SecurityFilterAutoConfiguration.class})
    static class ClientCredentialsTokenEndpointFilterEntryPointConfigurer {

        @SuppressWarnings({"rawtypes"})
        private final WebResponseExceptionTranslator exceptionTranslator;
        private final Filter springSecurityFilterChain;

        @SuppressWarnings({"rawtypes"})
        ClientCredentialsTokenEndpointFilterEntryPointConfigurer(
                @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain,
                WebResponseExceptionTranslator exceptionTranslator) {
            this.exceptionTranslator = exceptionTranslator;
            this.springSecurityFilterChain = springSecurityFilterChain;
        }

        @PostConstruct
        public void getFilters() {
            FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
            List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
            list.stream()
                    .flatMap(chain -> chain.getFilters().stream())
                    .forEach(filter -> {
                        if(filter instanceof ClientCredentialsTokenEndpointFilter) {
                            OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
                            authenticationEntryPoint.setTypeName("Form");
                            authenticationEntryPoint.setRealmName("oauth2/client");
                            authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);

                            ((ClientCredentialsTokenEndpointFilter)filter).setAuthenticationEntryPoint(authenticationEntryPoint);
                        }
                    });
        }
    }

}
