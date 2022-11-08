package icu.lowcoder.spring.cloud.authentication.web;

import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.util.AuthenticationUtils;
import icu.lowcoder.spring.cloud.authentication.web.model.AccountProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if (StringUtils.hasText(account.getAuthorities())) {
            accountProfile.setAuthorities(Stream.of(account.getAuthorities().split(","))
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList())
            );
        }

        return accountProfile;
    }
}
