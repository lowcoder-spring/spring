package icu.lowcoder.spring.commons.robot;

import icu.lowcoder.spring.commons.robot.tencent.TencentCaptchaRobotVerifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.robot-verification", name = "default-tester")
@ConditionalOnWebApplication
@EnableConfigurationProperties(RobotVerificationProperties.class)
public class RobotVerificationAutoConfiguration {

    @Bean
    @Primary
    RobotVerifier robotVerifier(
            RobotVerificationProperties robotTestProperties,
            @Autowired(required = false) TencentCaptchaRobotVerifierService tencentCaptchaRobotVerifierService
    ) {
        DelegateRobotVerifier delegateRobotTester = new DelegateRobotVerifier();
        // 目前只有腾讯验证码实现
        if (tencentCaptchaRobotVerifierService != null) {
            delegateRobotTester.addTester(TencentCaptchaRobotVerifierService.NAME, tencentCaptchaRobotVerifierService);
        }
        delegateRobotTester.setDefaultTester(robotTestProperties.getDefaultTester());

        return delegateRobotTester;
    }
}
