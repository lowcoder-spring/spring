package icu.lowcoder.spring.commons.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

@Slf4j
public class ServiceAuthenticateRequestInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    public ServiceAuthenticateRequestInterceptor(OAuth2RestTemplate serviceClientOAuth2RestTemplate) {
        this.serviceClientOAuth2RestTemplate = serviceClientOAuth2RestTemplate;
    }

    private final OAuth2RestTemplate serviceClientOAuth2RestTemplate;

    @Override
    public void apply(RequestTemplate template) {
        // 直接覆盖
        if (template.headers().containsKey(AUTHORIZATION_HEADER)) {
            log.debug("The authorization token will be override.");
        }

        boolean relayed = false;
        String accessToken = serviceClientOAuth2RestTemplate.getAccessToken().getValue();
        if (accessToken != null) {
            relayed = true;
            log.debug("The Authorization token has added in header, token:{}", accessToken);
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, accessToken));
        }

        if (!relayed) {
            log.debug("Not relay authorization token ass for service: {}", template.url());
        }

    }
}
