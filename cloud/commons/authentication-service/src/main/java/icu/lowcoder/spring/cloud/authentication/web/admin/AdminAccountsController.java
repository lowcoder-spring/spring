package icu.lowcoder.spring.cloud.authentication.web.admin;

import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.dao.specs.AccountSpecs;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.util.AuthenticationUtils;
import icu.lowcoder.spring.cloud.authentication.web.model.*;
import icu.lowcoder.spring.commons.sms.PhoneNumberUtils;
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
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/admin/accounts")
@RestController
public class AdminAccountsController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public Page<AdminAccountsListItem> listAccounts(
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
    public void updateAccountAuthorities(@PathVariable UUID accountId, @RequestBody UpdateAccountAuthoritiesRequest request) {
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
    public void updateAccountStatus(@PathVariable UUID accountId, @Valid @RequestBody UpdateAccountStatusRequest request) {
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

    @PostMapping
    @Transactional
    public UUIDIdResponse add(@Valid @RequestBody AddAccountRequest request) {
        if (!PhoneNumberUtils.isPhoneNumber(request.getPhone())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "请使用11位数字手机号");
        }

        //存在不抛出异常
        /*if (accountRepository.existsByPhone(request.getPhone())) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT, "已存在该手机号");
        }*/
        Account account;
        if (accountRepository.existsByPhone(request.getPhone())) {
            account = accountRepository.findByPhone(request.getPhone()).orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "未知错误"));
        } else {
            account = new Account();

            BeanUtils.copyProperties(request, account, "authorities");

            if (!CollectionUtils.isEmpty(request.getAuthorities())) {
                account.setAuthorities(StringUtils.collectionToCommaDelimitedString(request.getAuthorities()));
            }

            account.setRegisterTime(new Date());
            accountRepository.save(account);
        }

        return new UUIDIdResponse(account.getId());
    }

}
