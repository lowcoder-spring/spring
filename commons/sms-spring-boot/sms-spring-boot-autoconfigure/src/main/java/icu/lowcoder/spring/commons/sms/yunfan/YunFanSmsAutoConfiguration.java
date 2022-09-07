package icu.lowcoder.spring.commons.sms.yunfan;

import icu.lowcoder.spring.commons.sms.SmsAutoConfiguration;
import icu.lowcoder.spring.commons.sms.SmsProperties;
import icu.lowcoder.spring.commons.sms.SmsSender;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.sms.yunfan", name = "url")
@ConditionalOnMissingBean(SmsSender.class)
@EnableConfigurationProperties(YunFanSmsProperties.class)
@AutoConfigureAfter(SmsAutoConfiguration.class)
public class YunFanSmsAutoConfiguration {

    @Bean("yunFanSms")
    SmsSender yunFanSmsSender(YunFanSmsProperties yunFanSmsProperties, SmsProperties smsProperties) {
        return new YunFanSmsSender(yunFanSmsProperties, smsProperties);
    }
}
