package icu.lowcoder.spring.commons.wechat.cache.redis;

import icu.lowcoder.spring.commons.wechat.cache.DefaultTokenCacheService;
import icu.lowcoder.spring.commons.wechat.cache.TokenCacheService;
import icu.lowcoder.spring.commons.wechat.cache.WeChatTokenCacheProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.wechat.token-cache", name = "enabled")
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnMissingBean(value = TokenCacheService.class, ignored = DefaultTokenCacheService.class)
@ConditionalOnBean(StringRedisTemplate.class)
@EnableConfigurationProperties(WeChatTokenCacheProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisTokenCacheAutoConfiguration {

    @Bean
    @Primary
    TokenCacheService redisTokenCacheService(StringRedisTemplate redisTemplate, WeChatTokenCacheProperties cacheProperties) {
        return new RedisTokenCacheService(redisTemplate, cacheProperties);
    }

}
