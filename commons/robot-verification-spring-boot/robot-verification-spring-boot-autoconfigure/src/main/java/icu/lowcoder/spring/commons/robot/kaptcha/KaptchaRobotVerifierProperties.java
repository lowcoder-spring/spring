package icu.lowcoder.spring.commons.robot.kaptcha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.robot-verification.kaptcha")
public class KaptchaRobotVerifierProperties {

    private Properties properties = new Properties();

    private String imgEndpoint = "/captcha";

    private String reqParamName = "captcha";

    private KaptchaStrategy strategy = KaptchaStrategy.REDIS; // or SESSION

    private String requestIdHeader = "X-Kaptcha-Id";

    private String redisKey = "icu:lowcoder:spring:commons:robot-verification:kaptcha";

    private Long redisExpirationMinutes = 5L;

}
