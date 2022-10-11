package icu.lowcoder.spring.cloud.authentication.util;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

public class AuthenticationUtils {
    public static String currentUserId() {
        return currentUserId(true);
    }

    public static String currentUserId(boolean throwOnUnauthorized) {
        String userId = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication instanceof JwtAuthenticationToken) {
                userId = ((JwtAuthenticationToken) authentication).getToken().getSubject();
            }
        }

        if (!StringUtils.hasText(userId) && throwOnUnauthorized) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "未认证");
        }

        return userId;
    }
}
