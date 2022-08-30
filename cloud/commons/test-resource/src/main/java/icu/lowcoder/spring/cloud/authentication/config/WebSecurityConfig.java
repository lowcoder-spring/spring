package icu.lowcoder.spring.cloud.authentication.config;

import icu.lowcoder.spring.commons.exception.security.AccessDeniedExceptionHandler;
import icu.lowcoder.spring.commons.exception.security.UnifiedExceptionAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain resourceSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/public").permitAll()
                                .antMatchers("/admin").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedExceptionHandler())
                .authenticationEntryPoint(new UnifiedExceptionAuthenticationEntryPoint());
        return http.build();
    }
}
