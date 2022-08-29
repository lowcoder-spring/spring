package icu.lowcoder.spring.commons.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @see DefaultAccessTokenConverter
 */
public class CustomizedAccessTokenConverter extends DefaultAccessTokenConverter {

    final String CLIENT_AUTHORITIES = "client_authorities";

    /**
     * 新增加 client authorities
     * 当 authentication 为 userAuthentication 时返回客户端权限，用于后续验证
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) super.convertAccessToken(token, authentication);

        OAuth2Request clientToken = authentication.getOAuth2Request();
        if (!authentication.isClientOnly()) {
            if (clientToken.getAuthorities()!=null && !clientToken.getAuthorities().isEmpty()) {
                response.put(CLIENT_AUTHORITIES, AuthorityUtils.authorityListToSet(clientToken.getAuthorities()));
            }
        }

        return response;
    }

    /**
     * 读取 client authorities
     * 当 authentication 为 userAuthentication 时将 client authorities 添加至 oauth2Request
     */
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication oAuth2Authentication = super.extractAuthentication(map);
        if (!oAuth2Authentication.isClientOnly() && map.containsKey(CLIENT_AUTHORITIES)) {
            @SuppressWarnings("unchecked")
            String[] roles = ((Collection<String>)map.get(CLIENT_AUTHORITIES)).toArray(new String[0]);
            List<GrantedAuthority> clientAuthorities = AuthorityUtils.createAuthorityList(roles);

            OAuth2Request originalRequest = oAuth2Authentication.getOAuth2Request();
            OAuth2Request request = new OAuth2Request(
                    originalRequest.getRequestParameters(),
                    originalRequest.getClientId(),
                    clientAuthorities, // 替换 authorities
                    originalRequest.isApproved(),
                    originalRequest.getScope(),
                    originalRequest.getResourceIds(),
                    originalRequest.getRedirectUri(),
                    originalRequest.getResponseTypes(),
                    originalRequest.getExtensions());

            return new OAuth2Authentication(request, oAuth2Authentication.getUserAuthentication());
        }

        return oAuth2Authentication;
    }
}
