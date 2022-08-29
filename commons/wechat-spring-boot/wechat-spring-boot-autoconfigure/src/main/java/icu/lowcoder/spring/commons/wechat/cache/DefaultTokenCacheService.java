package icu.lowcoder.spring.commons.wechat.cache;

public class DefaultTokenCacheService implements TokenCacheService {
    @Override
    public String getToken(String appId) {
        return null;
    }

    @Override
    public void writeToken(String appId, String token, Long expiresIn) {
        // do nothing
    }
}
