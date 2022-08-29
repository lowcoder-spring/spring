package icu.lowcoder.spring.commons.logging.access.handler;

import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class SecurityContextPrincipalExtractor implements StringPrincipalExtractor {
    private final String[] PRINCIPAL_KEYS = new String[]{
            "id",
            "account",
            "account_id",
            "user",
            "username",
            "userid",
            "user_id",
            "login",
            "name"
    };

    @Override
    public String extract() {
        Object principal = getContextPrincipal();
        if (principal != null) {
            return extractFromMap(Collections.singletonMap("id", principal));
        } else {
            return null;
        }
    }

    @SuppressWarnings({"rawtypes"})
    protected String extractFromMap(Map data) {
        for (String key : PRINCIPAL_KEYS) {
            if (data.containsKey(key)) {
                Object principal = data.get(key);
                if (principal instanceof String) {
                    return (String) principal;
                } else if (principal instanceof UUID) {
                    return ((UUID) principal).toString();
                } else if (principal instanceof Number) {
                    return principal.toString();
                } else if (principal instanceof Map) {
                    return extractFromMap((Map) principal);
                } else if (principal instanceof Collection) {
                    return null;
                } else {
                    BeanMap beanMap = BeanMap.create(principal);
                    return extractFromMap(beanMap);
                }
            }
        }

        return null;
    }

    protected Object getContextPrincipal() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
}
