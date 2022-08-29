package icu.lowcoder.spring.commons.wechat.cache.redis;

import icu.lowcoder.spring.commons.wechat.cache.TicketCacheService;
import icu.lowcoder.spring.commons.wechat.cache.WeChatTicketCacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisTicketCacheService implements TicketCacheService {

    private final StringRedisTemplate redisTemplate;
    private final WeChatTicketCacheProperties cacheProperties;

    public RedisTicketCacheService(StringRedisTemplate stringRedisTemplate, WeChatTicketCacheProperties cacheProperties) {
        this.redisTemplate = stringRedisTemplate;
        this.cacheProperties = cacheProperties;
    }

    @Override
    public String getTicket(String appId) {
        String cacheKey = buildKey(appId);
        String accessToken = null;
        Boolean hasKey = redisTemplate.hasKey(cacheKey);
        if (hasKey != null && hasKey) {
            accessToken = redisTemplate.opsForValue().get(cacheKey);
        }

        return accessToken;
    }

    @Override
    public void writeTicket(String appId, String ticket, Long expiresIn) {
        String cacheKey = buildKey(appId);
        redisTemplate.opsForValue().set(cacheKey, ticket, expiresIn, TimeUnit.SECONDS);
    }

    private String buildKey(String appId) {
        return cacheProperties.getCacheKey() + "#" + appId;
    }

}
