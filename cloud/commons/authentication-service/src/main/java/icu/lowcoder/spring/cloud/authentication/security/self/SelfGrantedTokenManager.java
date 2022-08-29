package icu.lowcoder.spring.cloud.authentication.security.self;

public interface SelfGrantedTokenManager {
    SelfGrantedToken grant(AuthenticatedUser user);
}
