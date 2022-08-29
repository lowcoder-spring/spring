package icu.lowcoder.spring.commons.actuator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.management.security")
public class ActuatorSecurityProperties {
    private Boolean enabled = false;
}
