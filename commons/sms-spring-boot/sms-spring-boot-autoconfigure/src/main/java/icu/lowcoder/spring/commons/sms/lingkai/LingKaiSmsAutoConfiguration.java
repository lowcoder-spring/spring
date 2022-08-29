package icu.lowcoder.spring.commons.sms.lingkai;

import icu.lowcoder.spring.commons.sms.SmsAutoConfiguration;
import icu.lowcoder.spring.commons.sms.SmsProperties;
import icu.lowcoder.spring.commons.sms.SmsSender;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.sms.lingkai", name = "url")
@ConditionalOnMissingBean(SmsSender.class)
@EnableConfigurationProperties(LingKaiProperties.class)
@AutoConfigureAfter(SmsAutoConfiguration.class)
public class LingKaiSmsAutoConfiguration {

    @Bean("lingKaiSms")
    @Primary
    SmsSender lingKaiSmsSender(LingKaiProperties lingKaiProperties, SmsProperties smsProperties) {
        return new LingKaiSmsSender(lingKaiProperties, smsProperties);
    }
}
