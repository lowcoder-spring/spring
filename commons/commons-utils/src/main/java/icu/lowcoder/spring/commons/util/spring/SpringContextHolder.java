package icu.lowcoder.spring.commons.util.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    public static boolean isDevMode() {
        return applicationContext.getEnvironment().getProperty("icu.lowcoder.spring.commons.env.dev-mode", Boolean.class, false);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
