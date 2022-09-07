package icu.lowcoder.spring.cloud.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.cloud.auth")
public class AuthProperties {
    /**
     * token store key prefix, default value is 'icu.lowcoder.spring.cloud.auth:'
     */
    private String tokenStoreKeyPrefix = "icu.lowcoder.spring.cloud.auth:";

    private Integer minPasswordLength = 8;

    private Boolean smsAutoRegister = false;

    private String loginSmsTemplate = "短信验证码：#code#，请在#effective#分钟内输入";

    private String registerSmsTemplate = "短信验证码：#code#，请在#effective#分钟内输入";
}
