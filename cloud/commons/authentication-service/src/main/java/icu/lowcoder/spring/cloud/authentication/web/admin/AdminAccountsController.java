package icu.lowcoder.spring.cloud.authentication.web.admin;

import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.dao.specs.AccountSpecs;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.util.AuthenticationUtils;
import icu.lowcoder.spring.cloud.authentication.web.model.AdminAccountsListItem;
import icu.lowcoder.spring.cloud.authentication.web.model.UpdateAccountAuthoritiesRequest;
import icu.lowcoder.spring.cloud.authentication.web.model.UpdateAccountStatusRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/admin/accounts")
@RestController
public class AdminAccountsController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    Page<AdminAccountsListItem> listAccounts(
            String keyword,
            @PageableDefault(sort = "registerTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Account> accountPage = accountRepository.findAll(
                AccountSpecs.keywordMatch(keyword),
                pageable
        );

        return accountPage.map(account -> {
            AdminAccountsListItem item = new AdminAccountsListItem();
            BeanUtils.copyProperties(account, item, "authorities");
            if (StringUtils.hasText(account.getAuthorities())) {
                item.setAuthorities(Stream.of(account.getAuthorities().split(",")).toList());
            }
            return item;
        });
    }

    @PostMapping(path = "/{accountId}", params = "operate=updateAuthorities")
    @Transactional
    void updateAccountAuthorities(@PathVariable UUID accountId, @RequestBody UpdateAccountAuthoritiesRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "账户不存在"));

        // 忽略修改自己
        String userIdStr = AuthenticationUtils.currentUserId();
        UUID userId = UUID.fromString(userIdStr);
        if (accountId.equals(userId)) {
            return;
        }

        String authorities = null;
        if (!CollectionUtils.isEmpty(request.getAuthorities())) {
            authorities = request.getAuthorities().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.joining(","));
        }
        account.setAuthorities(authorities);
    }

    @PostMapping(path = "/{accountId}", params = "operate=updateStatus")
    @Transactional
    void updateAccountStatus(@PathVariable UUID accountId, @Valid @RequestBody UpdateAccountStatusRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "账户不存在"));

        // 忽略修改自己
        String userIdStr = AuthenticationUtils.currentUserId();
        UUID userId = UUID.fromString(userIdStr);
        if (accountId.equals(userId)) {
            return;
        }

        account.setEnabled(request.getEnabled());
    }
}
