package icu.lowcoder.spring.cloud.authentication.dao;

import icu.lowcoder.spring.cloud.authentication.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    Account findByPhoneOrEmail(String phone, String email);
}
