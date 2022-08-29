package icu.lowcoder.spring.commons.cache.redis;

import icu.lowcoder.spring.commons.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisCacheManager.class)
public class RedisCacheManagerConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager(CacheProperties cacheProperties, RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // default
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheProperties.getDefaultExpiration()));

        // cache names
        cacheProperties.getExpiration().forEach((name, expiration) -> {
            cacheConfigurations.put(name, defaultCacheConfiguration
                    .entryTtl(Duration.ofSeconds(expiration))
            );
        });

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
