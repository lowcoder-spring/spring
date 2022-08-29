package icu.lowcoder.spring.commons.ali.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.ali.oss", name = {"access-id", "access-key"})
@EnableConfigurationProperties(AliOssProperties.class)
public class AliOssAutoConfiguration {

    @Bean
    public OSS aliOssBackendClient(AliOssProperties aliOssProperties) {
        return new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessId(), aliOssProperties.getAccessKey(), aliOssProperties.getClient());
    }

    @Bean
    public AliOssClient aliOssClient(AliOssProperties aliOssProperties, OSS ossBackend) {
        return new AliOssClient(aliOssProperties, ossBackend);
    }

}
