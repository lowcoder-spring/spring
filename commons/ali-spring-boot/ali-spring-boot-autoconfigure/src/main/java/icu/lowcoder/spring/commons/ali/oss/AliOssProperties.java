package icu.lowcoder.spring.commons.ali.oss;


import com.aliyun.oss.ClientBuilderConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.ali.oss")
public class AliOssProperties {
    private String accessId;
    private String accessKey;
    private String defaultBucket; // 默认 bucket
    /**
     * 访问端点
     */
    private String endpoint;
    /**
     * 公网访问端点
     */
    private String extranetEndpoint;

    private String callbackUrl;
    private String callbackServicePrefix = "";
    private String defDir = "temp/";

    private Long uploadPolicyExpireSecond = 60L;

    /**
     * ali oss ClientBuilderConfiguration
     */
    private ClientBuilderConfiguration client;
}
