package icu.lowcoder.spring.commons.wechat.cache.redis;

import icu.lowcoder.spring.commons.wechat.cache.DefaultTicketCacheService;
import icu.lowcoder.spring.commons.wechat.cache.TicketCacheService;
import icu.lowcoder.spring.commons.wechat.cache.WeChatTicketCacheProperties;
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
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.wechat.ticket-cache", name = "enabled")
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnMissingBean(value = TicketCacheService.class, ignored = DefaultTicketCacheService.class)
@ConditionalOnBean(StringRedisTemplate.class)
@EnableConfigurationProperties(WeChatTicketCacheProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisTicketCacheAutoConfiguration {

    @Bean
    @Primary
    TicketCacheService redisTicketCacheService(StringRedisTemplate redisTemplate, WeChatTicketCacheProperties cacheProperties) {
        return new RedisTicketCacheService(redisTemplate, cacheProperties);
    }

}
