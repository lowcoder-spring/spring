package icu.lowcoder.spring.commons.wechat;

import icu.lowcoder.spring.commons.wechat.cache.TicketCacheService;
import icu.lowcoder.spring.commons.wechat.cache.TokenCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class WeChatAutoConfiguration {

    @Bean
    public WeChatClient weChatClient(TokenCacheService tokenCacheService, TicketCacheService ticketCacheService) {
        return new WeChatClient(tokenCacheService, ticketCacheService);
    }
}
