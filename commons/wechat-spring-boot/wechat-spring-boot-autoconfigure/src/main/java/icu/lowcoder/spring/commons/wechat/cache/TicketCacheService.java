package icu.lowcoder.spring.commons.wechat.cache;

public interface TicketCacheService {
    String getTicket(String appId);

    void writeTicket(String appId, String ticket, Long expiresIn);
}
