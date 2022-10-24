package icu.lowcoder.spring.commons.robot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.robot-verification", name = "default-tester")
@ConditionalOnWebApplication
@EnableConfigurationProperties(RobotVerificationProperties.class)
public class RobotVerificationAutoConfiguration {

    @Bean
    @Primary
    RobotVerifier robotVerifier(
            RobotVerificationProperties robotTestProperties,
            @Autowired(required = false) List<RobotVerifier> robotVerifiers
    ) {
        DelegateRobotVerifier delegateRobotTester = new DelegateRobotVerifier();
        if (robotVerifiers != null && !robotVerifiers.isEmpty()) {
            robotVerifiers.forEach(t -> delegateRobotTester.addTester(t.getName(), t));
        }
        // 用于测试场景
        if (robotTestProperties.getDefaultTester().equalsIgnoreCase("USELESS")) {
            delegateRobotTester.addTester("USELESS", new UselessRobotVerifier());
        }
        delegateRobotTester.setDefaultTester(robotTestProperties.getDefaultTester());
        return delegateRobotTester;
    }
}
