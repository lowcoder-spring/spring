package icu.lowcoder.spring.commons.security.expression;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestSecurityExpressionConfig {

    @Bean("requestSecurity")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public RequestSecurityExpressionMethods requestWebSecurityExpressionHandler() {
        return new RequestSecurityExpressionMethods();
    }

}
