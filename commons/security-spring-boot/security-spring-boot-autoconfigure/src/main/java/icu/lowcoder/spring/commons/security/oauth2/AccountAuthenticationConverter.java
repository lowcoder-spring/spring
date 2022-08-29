package icu.lowcoder.spring.commons.security.oauth2;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @see org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter
 */
public class AccountAuthenticationConverter implements UserAuthenticationConverter {

    private Collection<? extends GrantedAuthority> defaultAuthorities;

    private UserDetailsService userDetailsService;

    private PrincipalExtractor principalExtractor = new FixedPrincipalExtractor();

    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setDefaultAuthorities(Collection<? extends GrantedAuthority> defaultAuthorities) {
        this.defaultAuthorities = defaultAuthorities;
    }

    public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {
        this.principalExtractor = principalExtractor;
    }

    public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {
        this.authoritiesExtractor = authoritiesExtractor;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, authentication.getName());

        Object principal = authentication.getPrincipal();
        if (principal != null) {
            if (principal instanceof Map) {
                response.putAll((Map<? extends String, ?>) principal);
            } else if (principal instanceof UserDetails) {
                response.putAll(BeanMap.create(authentication.getPrincipal()));
            }
        }

        response.remove("password");

        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }

        return response;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Authentication extractAuthentication(Map<String, ?> map) {
        Object principal = principalExtractor.extractPrincipal((Map<String, Object>) map);
        if (principal != null) {
            Collection<? extends GrantedAuthority> authorities = authoritiesExtractor.extractAuthorities((Map<String, Object>) map);
            if (userDetailsService != null) {
                UserDetails user = userDetailsService.loadUserByUsername((String) map.get(USERNAME));
                authorities = user.getAuthorities();
                principal = user;
            }
            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }

}
