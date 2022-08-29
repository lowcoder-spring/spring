package icu.lowcoder.spring.commons.ali.oss.sts;

import icu.lowcoder.spring.commons.ali.oss.AliOssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.ali.oss.sts", name = "role-arn")
@ConditionalOnBean(AliOssProperties.class)
@EnableConfigurationProperties(AliOssStsProperties.class)
public class AliOssStsAutoConfiguration {

    @Bean
    public AliOssStsManager aliOssStsManager(AliOssProperties aliOssProperties, AliOssStsProperties aliOssStsProperties) {
        return new AliOssStsManager(aliOssProperties, aliOssStsProperties);
    }

}
