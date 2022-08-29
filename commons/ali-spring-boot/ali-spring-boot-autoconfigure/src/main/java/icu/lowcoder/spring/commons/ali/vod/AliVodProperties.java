package icu.lowcoder.spring.commons.ali.vod;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.ali.vod")
public class AliVodProperties {
    private String accessId;
    private String accessKey;
    private String regionId = "cn-shanghai"; // 点播服务接入区域
    private String rootCategoryPrefix = ""; // 顶级分类前缀
}
