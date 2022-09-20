package icu.lowcoder.spring.cloud.resource.server;

import icu.lowcoder.spring.cloud.resource.server.jwt.JwtProtectedAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(JwtProtectedAutoConfiguration.class)
public class ResourceServerAutoConfiguration {

}
