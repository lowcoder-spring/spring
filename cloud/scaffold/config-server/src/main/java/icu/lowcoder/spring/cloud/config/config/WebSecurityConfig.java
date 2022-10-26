package icu.lowcoder.spring.cloud.config.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {
    @Value("${icu.lowcoder.spring.commons.management.security.enabled:true}")
    private boolean managementSecurityEnabled;

    @Bean
    public SecurityFilterChain configServerSecurityFilterChain(HttpSecurity http) throws Exception {
        if (managementSecurityEnabled) {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and().formLogin()
                    .and().httpBasic()
                    .and().csrf().disable();
        } else {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and().csrf().disable();
        }
        return http.build();
    }
}
