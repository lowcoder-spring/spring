package icu.lowcoder.spring.commons.jpa.auditing;

import icu.lowcoder.spring.commons.jpa.JpaAuditingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@Slf4j
public class JwtAuthenticationAuditorExtractor extends AuthenticationAuditorExtractor {

    private final JpaAuditingProperties auditingProperties;

    public JwtAuthenticationAuditorExtractor(JpaAuditingProperties auditingProperties) {
        super(auditingProperties);
        this.auditingProperties = auditingProperties;
    }

    @Override
    protected Object extractFromAuthentication(Authentication authentication) {
        Object auditor = null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            auditor = ((Jwt) principal).getSubject();
            if (auditor == null) {
                Map<String, Object> claims = ((Jwt) principal).getClaims();
                String[] keys = auditingProperties.getAuditorKey().split(",");
                for (String key : keys) {
                    if (claims.containsKey(key)) {
                        auditor = claims.get(key);
                        break;
                    }
                }
            }
        }


        if (auditor == null) {
            auditor = super.extractFromAuthentication(authentication);
        }

        return auditor;
    }

}
