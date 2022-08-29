package icu.lowcoder.spring.cloud.authentication.security.self;

import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.commons.util.spring.BeanUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Setter
@NoArgsConstructor
public class AuthenticatedUser implements UserDetails {

    @Getter
    private String userId;
    @Getter
    private String nickname;
    @Getter
    private String phone;
    private String password;
    private Boolean enabled = true;
    private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

    public static AuthenticatedUser create(Account account) {
        return create(account, null);
    }

    public static AuthenticatedUser create(Account account, Collection<? extends GrantedAuthority> authorities) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        BeanUtils.copyProperties(account, authenticatedUser, "authorities");
        authenticatedUser.setUserId(account.getId().toString());
        authenticatedUser.setNickname(account.getName());

        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (authorities != null) {
            authorityList.addAll(authorities);
        }
        if (StringUtils.hasText(account.getAuthorities())) {
            authorityList.addAll(Stream.of(account.getAuthorities().split(",")).map(SimpleGrantedAuthority::new).toList());
        }
        authenticatedUser.setAuthorities(authorityList);

        return authenticatedUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
