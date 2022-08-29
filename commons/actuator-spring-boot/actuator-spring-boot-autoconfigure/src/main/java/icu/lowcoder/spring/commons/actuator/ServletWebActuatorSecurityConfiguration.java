package icu.lowcoder.spring.commons.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SecurityFilterChain.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletWebActuatorSecurityConfiguration {

    @Slf4j
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Configuration(proxyBeanMethods = false)
    static class ActuatorRequestSecurityConfiguration {

        private final ActuatorSecurityProperties actuatorSecurityProperties;
        private final WebEndpointProperties webEndpointProperties;
        private final SecurityProperties securityProperties;


        ActuatorRequestSecurityConfiguration(ActuatorSecurityProperties actuatorSecurityProperties, WebEndpointProperties webEndpointProperties, SecurityProperties securityProperties) {
            this.actuatorSecurityProperties = actuatorSecurityProperties;
            this.webEndpointProperties = webEndpointProperties;
            this.securityProperties = securityProperties;
        }

        /*
        @Bean
        @ConditionalOnBean(SecurityProperties.class)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
        */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }

        /*@Override
        @ConditionalOnBean(SecurityProperties.class)
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(inMemoryUserDetailsService());
        }*/
        public InMemoryUserDetailsManager userDetailsService() {
            SecurityProperties.User user = securityProperties.getUser();
            List<String> roles = user.getRoles();
            return new InMemoryUserDetailsManager(
                    User.withUsername(user.getName())
                            .password(getOrDeducePassword(user))
                            .roles(StringUtils.toStringArray(roles))
                            .build()
            );
        }

        /*
        private UserDetailsService inMemoryUserDetailsService() {
            SecurityProperties.User user = securityProperties.getUser();
            List<String> roles = user.getRoles();
            return new InMemoryUserDetailsManager(
                    User.withUsername(user.getName()).password(getOrDeducePassword(user))
                            .roles(StringUtils.toStringArray(roles)).build());
        }
        */

        private String getOrDeducePassword(SecurityProperties.User user) {
            String password = user.getPassword();
            if (user.isPasswordGenerated()) {
                log.info(String.format("%n%nUsing generated security password: %s%n", user.getPassword()));
            }
            return "{noop}" + password;
        }

        /*
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            if (actuatorSecurityProperties.getEnabled()) {
                http.antMatcher(webEndpointProperties.getBasePath() + "/**")
                        .authorizeRequests().anyRequest().authenticated()
                        .and().formLogin()
                        .and().httpBasic()
                        .and().csrf().disable();
            } else {
                http.antMatcher(webEndpointProperties.getBasePath() + "/**")
                        .authorizeRequests().anyRequest().permitAll()
                        .and().formLogin()
                        .and().httpBasic()
                        .and().csrf().disable();
            }
        }
        */

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            if (actuatorSecurityProperties.getEnabled()) {
                AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder.userDetailsService(userDetailsService());
                AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

                http.antMatcher(webEndpointProperties.getBasePath() + "/**")
                        .authorizeRequests().anyRequest().authenticated()
                        .and().authenticationManager(authenticationManager)
                        .formLogin()
                        .and().httpBasic()
                        .and().csrf().disable();
            } else {
                http.antMatcher(webEndpointProperties.getBasePath() + "/**")
                        .authorizeRequests().anyRequest().permitAll()
                        .and().formLogin()
                        .and().httpBasic()
                        .and().csrf().disable();
            }
            return http.build();
        }
    }

}
