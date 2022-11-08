package icu.lowcoder.spring.cloud.authentication.web;

import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.util.AuthenticationUtils;
import icu.lowcoder.spring.cloud.authentication.web.model.AccountProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/profile")
    public AccountProfile profile() {
        String userIdStr = AuthenticationUtils.currentUserId();
        UUID userId = UUID.fromString(userIdStr);
        Account account = accountRepository.findById(userId).orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "获取账户信息失败"));

        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setUserId(account.getId());
        accountProfile.setName(account.getName());
        accountProfile.setEmail(account.getEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (!CollectionUtils.isEmpty(authorities)) {
            accountProfile.setAuthorities(authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())
            );
        }
        return accountProfile;
    }
}
