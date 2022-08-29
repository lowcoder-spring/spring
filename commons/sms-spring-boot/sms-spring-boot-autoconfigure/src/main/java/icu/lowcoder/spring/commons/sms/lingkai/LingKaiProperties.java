package icu.lowcoder.spring.commons.sms.lingkai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.sms.lingkai")
public class LingKaiProperties {
    private String url;
    private String encoding = "utf-8";
    private String sign = "【SIGN】";
    private String unsubscribe = "，退订回N";
}
