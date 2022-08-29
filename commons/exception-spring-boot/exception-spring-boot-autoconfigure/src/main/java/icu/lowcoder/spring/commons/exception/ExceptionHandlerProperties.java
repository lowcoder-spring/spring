package icu.lowcoder.spring.commons.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.exception")
public class ExceptionHandlerProperties {
    private boolean showDetail = false;
    private boolean printStackTrace = false;
}
