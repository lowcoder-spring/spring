package icu.lowcoder.spring.cloud.authentication.web;

import icu.lowcoder.spring.cloud.authentication.config.WeChatProperties;
import icu.lowcoder.spring.cloud.authentication.dao.AccountRepository;
import icu.lowcoder.spring.cloud.authentication.dao.WeChatConnectionRepository;
import icu.lowcoder.spring.cloud.authentication.dict.WeChatAppType;
import icu.lowcoder.spring.cloud.authentication.entity.Account;
import icu.lowcoder.spring.cloud.authentication.entity.WeChatConnection;
import icu.lowcoder.spring.cloud.authentication.util.AuthenticationUtils;
import icu.lowcoder.spring.cloud.authentication.web.model.AddWeChatWebAppConnectionRequest;
import icu.lowcoder.spring.cloud.authentication.web.model.WeChatConnectionResponse;
import icu.lowcoder.spring.commons.util.spring.BeanUtils;
import icu.lowcoder.spring.commons.wechat.WeChatClient;
import icu.lowcoder.spring.commons.wechat.model.WebUserAccessToken;
import icu.lowcoder.spring.commons.wechat.model.WebUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RequestMapping("/we-chat/connections")
@RestController
public class WeChatConnectionsController {

    @Autowired
    private WeChatConnectionRepository weChatConnectionRepository;
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private WeChatClient weChatClient;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @PostMapping(params = "appType=WEB_APP")
    public WeChatConnectionResponse addWebAppConnection(@Valid @RequestBody AddWeChatWebAppConnectionRequest request) {
        String userIdStr = AuthenticationUtils.currentUserId();
        UUID userId = UUID.fromString(userIdStr);
        Account account = accountRepository.findById(userId).orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "获取账户信息失败"));

        WeChatProperties.App weChatApp = weChatProperties.getApps()
                .stream()
                .filter(app -> app.getAppId().equals(request.getAppId()))
                .findFirst()
                .orElseThrow(() -> new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "WeChatApp:" + request.getAppId() + " has not configured."));

        // code 获取用户信息
        try {
            // token
            WebUserAccessToken webUserAccessToken = weChatClient.getWebUserAccessToken(request.getCode(), request.getAppId(), weChatApp.getSecret());
            // user info
            WebUserInfo webUserInfo;
            if(webUserAccessToken.getScope().contains("snsapi_userinfo")) {
                webUserInfo = weChatClient.getWebUserInfo(webUserAccessToken.getAccessToken(), webUserAccessToken.getOpenid());
            } else {
                webUserInfo = new WebUserInfo();
                webUserInfo.setOpenid(webUserAccessToken.getOpenid());
            }

            // 已绑定信息查询
            WeChatConnection connection = weChatConnectionRepository.findOneByAccountAndAppId(account, request.getAppId());
            if (connection == null) {
                // 新增绑定
                connection = new WeChatConnection();
                connection.setAccount(account);
                connection.setUnionId(webUserAccessToken.getUnionid());
                connection.setOpenId(webUserInfo.getOpenid());
                connection.setAppType(WeChatAppType.WEB_APP);
                connection.setAppId(request.getAppId());

                connection.setNickname(webUserInfo.getNickname());
                connection.setGender(webUserInfo.getSex());
                connection.setProvince(webUserInfo.getProvince());
                connection.setCity(webUserInfo.getCity());
                connection.setCountry(webUserInfo.getCountry());
                connection.setAvatar(webUserInfo.getHeadimgurl());

                weChatConnectionRepository.save(connection);
                return BeanUtils.instantiate(WeChatConnectionResponse.class, connection, "account");
            } else {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "已绑定微信");
            }
        } catch (Exception e) {
            log.warn("获取微信用户信息出现异常:" + e.getMessage(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "获取微信用户信息失败");
        }
    }

    @GetMapping(path = "/{appId}")
    public WeChatConnectionResponse getConnection(@PathVariable String appId) {
        if (!StringUtils.hasText(appId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "The appId must not be empty.");
        }

        WeChatConnection connection = weChatConnectionRepository.findOneByAppId(appId)
                .orElse(null);

        if (connection != null) {
            return BeanUtils.instantiate(WeChatConnectionResponse.class, connection, "account");
        }
        return null;
    }


}
