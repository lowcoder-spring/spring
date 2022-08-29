package icu.lowcoder.spring.commons.logging.access;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "icu.lowcoder.spring.commons.logging.access")
public class AccessLoggingProperties {
    private Boolean enabled = false;
    private List<String> ignoreParams = Arrays.asList(
            "password",
            "key",
            "access_token",
            "accessToken",
            "token",
            "secret",
            "secret_key",
            "secretKey"
    );
    private List<String> excludedUrls = Arrays.asList(
            "/actuator/**"
    );
    private String ignoreParamsReplacement = "******";
    private List<String> recordRequestHeaders = Collections.singletonList("user-agent");

    private Boolean deserializeBody = false;
    private Boolean enabledRecordRequestBody = true;
    private Boolean enabledRecordResponseBody = false;
    private Boolean content = false;
    private List<String> recordBodyTypes = Arrays.asList(
            MediaType.APPLICATION_JSON_VALUE,
            "text/*",
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    );
    private long maxPayloadLength = 1024;

    private List<String> recordResponseHeaders = Collections.emptyList();
}
