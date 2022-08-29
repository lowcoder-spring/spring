package icu.lowcoder.spring.commons.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.cache")
public class CacheProperties {

    /**
     * Use Duration.ZERO to declare an eternal cache.
     */
    private Map<String, Long> expiration = new HashMap<String, Long>(){{
        put("icu.lowcoder.spring", 1L * 10 * 60);
    }};

    private Long defaultExpiration = 1L * 24 * 60 * 60;

    private Boolean enabled = true;
}
