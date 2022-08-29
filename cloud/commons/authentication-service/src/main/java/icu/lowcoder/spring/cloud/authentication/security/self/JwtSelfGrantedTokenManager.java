package icu.lowcoder.spring.cloud.authentication.security.self;

import icu.lowcoder.spring.cloud.authentication.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class JwtSelfGrantedTokenManager implements SelfGrantedTokenManager {
    public static final String SCOPE_CLAIM_NAME = "scope";

    @Autowired
    private JwtEncoder encoder;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public SelfGrantedToken grant(AuthenticatedUser user) {
        Instant now = Instant.now();
        long expiry = jwtProperties.getExpirySeconds();

        // @formatter:off
        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getUserId())
                .claim(SCOPE_CLAIM_NAME, scope)
                .build();
        // @formatter:on

        SelfGrantedToken token = new SelfGrantedToken();
        token.setAccessToken(encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());

        return token;
    }
}
