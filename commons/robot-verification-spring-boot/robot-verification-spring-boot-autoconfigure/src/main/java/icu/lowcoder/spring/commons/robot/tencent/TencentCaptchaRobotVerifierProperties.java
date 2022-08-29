package icu.lowcoder.spring.commons.robot.tencent;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.robot-verification.tencent")
public class TencentCaptchaRobotVerifierProperties {
    private String appId;
    private String appSecretKey;
}
