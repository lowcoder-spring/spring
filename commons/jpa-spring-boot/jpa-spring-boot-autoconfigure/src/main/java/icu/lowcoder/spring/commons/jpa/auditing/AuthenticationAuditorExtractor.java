package icu.lowcoder.spring.commons.jpa.auditing;

import icu.lowcoder.spring.commons.jpa.JpaAuditingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class AuthenticationAuditorExtractor {

    private final JpaAuditingProperties auditingProperties;

    public AuthenticationAuditorExtractor(JpaAuditingProperties auditingProperties) {
        this.auditingProperties = auditingProperties;
    }

    public Object extractor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object auditor = null;
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            // 简单数据类型
            if (principal instanceof UUID ||
                    principal instanceof String ||
                    principal instanceof Integer ||
                    principal instanceof Long) {
                auditor = principal;
            } else {
                if (!StringUtils.isEmpty(auditingProperties.getAuditorKey())) {
                    String[] keys = auditingProperties.getAuditorKey().split(",");

                    if (principal instanceof Map) {
                        Map<?, ?> properties = ((Map<?, ?>) principal);
                        for (String key : keys) {
                            if (properties.containsKey(key)) {
                                auditor = properties.get(key);
                                break;
                            }
                        }
                    } else {
                        BeanMap beanMap = BeanMap.create(principal);
                        for (String key : keys) {
                            if (beanMap.containsKey(key)) {
                                auditor = beanMap.get(key);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (auditor == null) {
            log.warn("can't extractor auditor, key: {}", auditingProperties.getAuditorKey());
        }

        return auditor;
    }
}
