package icu.lowcoder.spring.commons.wechat.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DefaultCacheAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.wechat.ticket-cache", name = "enabled", havingValue = "false", matchIfMissing = true)
    static public class TicketCacheAutoConfiguration {

        @Bean
        public TicketCacheService doNothingTicketCacheService() {
            return new DefaultTicketCacheService();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.wechat.token-cache", name = "enabled", havingValue = "false", matchIfMissing = true)
    static public class TokenCacheAutoConfiguration {

        @Bean
        public TokenCacheService doNothingTokenCacheService() {
            return new DefaultTokenCacheService();
        }

    }

}
