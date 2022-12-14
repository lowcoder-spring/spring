package icu.lowcoder.spring.commos.cloudapi;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.cloudapi")
public class CloudApiProperties {
    private Map<ApiName, Api> apis = new HashMap<>();

    private Integer readTimeout = 60000;
    private Integer connectTimeout = 60000;
    private Boolean throwOnResponseIsNotSuccessful = false;

    @Getter
    @Setter
    public static class Api {
        private String defaultProvider;
        private Map<String, ApiProvider> providers = new HashMap<>();
    }

    @Getter
    @Setter
    public static class ApiProvider {
        private Boolean enabled = true;
        private Map<String, String> meta = new HashMap<>();
    }
}
