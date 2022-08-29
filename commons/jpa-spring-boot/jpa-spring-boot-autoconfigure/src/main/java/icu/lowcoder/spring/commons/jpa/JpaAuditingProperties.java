package icu.lowcoder.spring.commons.jpa;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.jpa.auditing")
public class JpaAuditingProperties {
    private boolean enabled = true;
    private String auditorKey = "id,username";
}
