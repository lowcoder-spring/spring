package icu.lowcoder.spring.commons.util.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ApplicationContext.class)
public class SpringContextHolderAutoConfiguration {
    @Bean
    public SpringContextHolder contextHolder() {
        return new SpringContextHolder();
    }
}
