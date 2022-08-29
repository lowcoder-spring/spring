package icu.lowcoder.spring.commons.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Set;

public class SecurityUtils {

    public static String getPrincipalId() {
        return getPrincipalId(true);
    }

    public static String getPrincipalId(boolean throwExceptionOnUnauthorized) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AccountModel) {
                return String.valueOf(((AccountModel) principal).getId());
            } else if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return authentication.getPrincipal().toString();
            }
        } else if (throwExceptionOnUnauthorized) {
            throw new AuthenticationCredentialsNotFoundException("can't get principal.");
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrincipal(Class<T> type, boolean throwExceptionOnUnauthorized, boolean throwExceptionOnTypeNotMatch) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (type.isAssignableFrom(principal.getClass())) {
                return (T) principal;
            } else {
                if (throwExceptionOnTypeNotMatch) {
                    throw new ClassCastException(principal.getClass().getName() + " can't cast to " + type.getName());
                }

                return null;
            }
        } else if (throwExceptionOnUnauthorized) {
            throw new AuthenticationCredentialsNotFoundException("Can't get principal.");
        } else {
            return null;
        }
    }

    public static <T> T getPrincipal(Class<T> type) {
        return getPrincipal(type, true, true);
    }

    public static UserDetails getPrincipal() {
        return getPrincipal(UserDetails.class, true, false);
    }

    public static Set<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            return AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        } else {
            return Collections.emptySet();
        }
    }
}