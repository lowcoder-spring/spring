package icu.lowcoder.spring.commons.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.logging")
public class LoggingProperties {

    private String logstashEndpoint = "logstash:4560";
    private String rootLevel = "INFO";

}
