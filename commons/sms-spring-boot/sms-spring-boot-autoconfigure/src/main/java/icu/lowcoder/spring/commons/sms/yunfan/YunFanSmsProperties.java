package icu.lowcoder.spring.commons.sms.yunfan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.sms.yunfan")
public class YunFanSmsProperties {
    private String appKey;
    private String appCode;
    private String appSecret;
    private String url;
}
