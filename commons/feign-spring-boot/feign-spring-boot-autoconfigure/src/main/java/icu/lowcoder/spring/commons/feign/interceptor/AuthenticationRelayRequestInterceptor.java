package icu.lowcoder.spring.commons.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@Slf4j
public class AuthenticationRelayRequestInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        if (template.headers().containsKey(AUTHORIZATION_HEADER)) {
            log.debug("The authorization token will be override.");
        }

        boolean relayed = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AbstractAuthenticationToken && !(authentication instanceof AnonymousAuthenticationToken)) {
            AbstractAuthenticationToken aat = (AbstractAuthenticationToken) authentication;
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) aat.getDetails();

            String type = details.getTokenType();
            String accessToken = details.getTokenValue();

            if (OAuth2AccessToken.BEARER_TYPE.equalsIgnoreCase(type) && accessToken != null) {
                relayed = true;
                log.debug("The Authorization token has added in header, token:{}", accessToken);
                template.header("Authorization", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
            }
        }

        if (!relayed) {
            log.debug("Not relay authorization token ass for service: {}", template.url());
        }
    }
}
