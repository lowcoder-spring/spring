package icu.lowcoder.spring.commons.wechat.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.wechat.token-cache")
public class WeChatTokenCacheProperties {
    private Boolean enabled;
    private String cacheKey = "icu.lowcoder.spring.commons.wechat.token";
}
