package icu.lowcoder.spring.commons.actuator;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;



@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SecurityWebFilterChain.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean(SecurityWebFilterChain.class)
public class ReactiveWebActuatorSecurityConfiguration {

    @EnableWebFluxSecurity
    static class ActuatorRequestSecurityConfiguration {
        private final ActuatorSecurityProperties actuatorSecurityProperties;
        private final WebEndpointProperties webEndpointProperties;

        ActuatorRequestSecurityConfiguration(ActuatorSecurityProperties actuatorSecurityProperties, WebEndpointProperties webEndpointProperties) {
            this.actuatorSecurityProperties = actuatorSecurityProperties;
            this.webEndpointProperties = webEndpointProperties;
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {
            if (actuatorSecurityProperties.getEnabled()) {
                http.authorizeExchange(exchange -> exchange.pathMatchers(webEndpointProperties.getBasePath() + "/**").authenticated())
                        .httpBasic()
                        .and().httpBasic()
                        .and().formLogin()
                        .and().csrf().disable();
            } else {
                http.authorizeExchange(exchange -> exchange.pathMatchers(webEndpointProperties.getBasePath() + "/**").permitAll());
            }

            return http.build();
        }
    }

}
