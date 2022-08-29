package icu.lowcoder.spring.commons.wechat.cache.redis;

import icu.lowcoder.spring.commons.wechat.cache.TokenCacheService;
import icu.lowcoder.spring.commons.wechat.cache.WeChatTokenCacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisTokenCacheService implements TokenCacheService {

    private final StringRedisTemplate redisTemplate;
    private final WeChatTokenCacheProperties cacheProperties;

    public RedisTokenCacheService(StringRedisTemplate stringRedisTemplate, WeChatTokenCacheProperties cacheProperties) {
        this.redisTemplate = stringRedisTemplate;
        this.cacheProperties = cacheProperties;
    }

    @Override
    public String getToken(String appId) {
        String cacheKey = buildKey(appId);
        String accessToken = null;
        Boolean hasKey = redisTemplate.hasKey(cacheKey);
        if (hasKey != null && hasKey) {
            accessToken = redisTemplate.opsForValue().get(cacheKey);
        }

        return accessToken;
    }

    @Override
    public void writeToken(String appId, String token, Long expiresIn) {
        String cacheKey = buildKey(appId);
        redisTemplate.opsForValue().set(cacheKey, token, expiresIn, TimeUnit.SECONDS);
    }

    private String buildKey(String appId) {
        return cacheProperties.getCacheKey() + "#" + appId;
    }

}
