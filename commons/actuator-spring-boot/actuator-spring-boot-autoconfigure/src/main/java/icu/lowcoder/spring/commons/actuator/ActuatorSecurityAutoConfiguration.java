package icu.lowcoder.spring.commons.actuator;


import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(WebEndpointProperties.class)
@EnableConfigurationProperties(ActuatorSecurityProperties.class)
@Import({ ServletWebActuatorSecurityConfiguration.class, ReactiveWebActuatorSecurityConfiguration.class })
public class ActuatorSecurityAutoConfiguration {

}
