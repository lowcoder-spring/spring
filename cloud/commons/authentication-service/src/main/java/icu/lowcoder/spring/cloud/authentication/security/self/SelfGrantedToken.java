package icu.lowcoder.spring.cloud.authentication.security.self;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfGrantedToken {
    private String accessToken;
    private String refreshToken;
}
