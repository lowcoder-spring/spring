package icu.lowcoder.spring.commons.baidu.map;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.baidu.map")
public class BaiduMapProperties {
    /**
     * 百度地图访问ak
     */
    private String ak;

}
