package icu.lowcoder.spring.cloud.authentication.dao;

import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.entity.WeChatConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface WeChatConnectionRepository extends JpaRepository<WeChatConnection, UUID>, JpaSpecificationExecutor<WeChatConnection> {
    Optional<WeChatConnection> findOneByAppId(String appId);

    WeChatConnection findOneByAccountAndAppId(Account account, String appId);
}