package icu.lowcoder.spring.commons.wechat.cache;

public interface TokenCacheService {
    String getToken(String appId);

    void writeToken(String appId, String token, Long expiresIn);
}
