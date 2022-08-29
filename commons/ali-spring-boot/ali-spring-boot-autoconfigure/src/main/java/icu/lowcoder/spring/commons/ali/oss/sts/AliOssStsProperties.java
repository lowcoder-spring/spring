package icu.lowcoder.spring.commons.ali.oss.sts;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.ali.oss.sts")
public class AliOssStsProperties {
    private String endpoint = "sts-vpc.cn-hangzhou.aliyuncs.com";
    private String roleArn;
    private Long durationSeconds = 900L;
}
