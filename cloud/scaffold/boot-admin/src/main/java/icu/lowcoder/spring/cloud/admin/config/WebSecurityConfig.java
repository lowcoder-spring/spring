package icu.lowcoder.spring.cloud.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {
    @Value("${icu.lowcoder.spring.commons.management.security.enabled:true}")
    private boolean managementSecurityEnabled;

    @Autowired
    private AdminServerProperties adminServerProperties;

    @Bean
    public SecurityFilterChain bootAdminSecurityFilterChain(HttpSecurity http) throws Exception {
        if (managementSecurityEnabled) {
            String adminContextPath = adminServerProperties.getContextPath();

            SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
            successHandler.setTargetUrlParameter("redirectTo");
            successHandler.setDefaultTargetUrl(adminContextPath + "/");

            http
                    .authorizeRequests()
                    .antMatchers(adminContextPath + "/assets/**").permitAll()
                    .antMatchers(adminContextPath + "/login").permitAll()
                    .anyRequest().authenticated()
                    .and().formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler)
                    .and().logout().logoutUrl(adminContextPath + "/logout")
                    .and().httpBasic()
                    .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(
                            "/instances",
                            "/actuator/**"
                    );
        } else {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and().csrf().disable();
        }
        return http.build();
    }
}
