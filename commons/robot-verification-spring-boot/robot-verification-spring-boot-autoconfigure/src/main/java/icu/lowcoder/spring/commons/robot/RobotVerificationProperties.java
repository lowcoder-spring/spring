package icu.lowcoder.spring.commons.robot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.robot-verification")
public class RobotVerificationProperties {
    private String defaultTester;
}
