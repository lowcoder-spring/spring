package icu.lowcoder.spring.commons.sms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.sms")
public class SmsProperties {
    private int maxBatchSize = 50;
}
