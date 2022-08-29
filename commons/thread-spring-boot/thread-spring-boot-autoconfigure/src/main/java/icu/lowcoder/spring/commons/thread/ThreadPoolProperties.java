package icu.lowcoder.spring.commons.thread;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.thread")
public class ThreadPoolProperties {
    private int size = 5;
    private int maxSize = 10;
    private int queueCapacity = 25;
}
