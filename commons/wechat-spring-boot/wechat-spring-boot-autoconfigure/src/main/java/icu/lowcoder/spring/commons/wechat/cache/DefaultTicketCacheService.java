package icu.lowcoder.spring.commons.wechat.cache;

public class DefaultTicketCacheService implements TicketCacheService {
    @Override
    public String getTicket(String appId) {
        return null;
    }

    @Override
    public void writeTicket(String appId, String ticket, Long expiresIn) {

    }
}
