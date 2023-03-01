package icu.lowcoder.spring.cloud.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.cloud.auth.we-chat")
public class WeChatProperties {

    private List<App> apps = new ArrayList<>();

    @Getter
    @Setter
    public static class App {
        private String appId;
        private String secret;
        private String description;
    }
}
