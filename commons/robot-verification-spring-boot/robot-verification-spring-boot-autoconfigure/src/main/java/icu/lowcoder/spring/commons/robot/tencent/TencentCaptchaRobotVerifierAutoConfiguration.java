package icu.lowcoder.spring.commons.robot.tencent;

import icu.lowcoder.spring.commons.robot.RobotVerificationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.robot-verification.tencent", name = {"app-id", "app-secret-key"})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(TencentCaptchaRobotVerifierProperties.class)
@AutoConfigureBefore(RobotVerificationAutoConfiguration.class)
public class TencentCaptchaRobotVerifierAutoConfiguration {

    @Bean("tencentRobotVerifier")
    TencentCaptchaRobotVerifierService tencentRobotVerifier(TencentCaptchaRobotVerifierProperties properties) {
        return new TencentCaptchaRobotVerifierService(properties.getAppId(), properties.getAppSecretKey());
    }
}
