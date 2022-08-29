package icu.lowcoder.spring.commons.wechat;

import icu.lowcoder.spring.commons.wechat.cache.TicketCacheService;
import icu.lowcoder.spring.commons.wechat.cache.TokenCacheService;
import icu.lowcoder.spring.commons.wechat.http.TextJackson2HttpMessageConverter;
import icu.lowcoder.spring.commons.wechat.model.*;
import icu.lowcoder.spring.commons.wechat.util.WeChatAESInfoDecoder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeChatClient {
    // 公众号相关, snake case
    private static final String WEB_API_GET_USER_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appId}&secret={secret}&code={code}&grant_type=authorization_code";
    private static final String WEB_API_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token={accessToken}&openid={openId}&lang=zh_CN";
    private static final String WEB_API_SEND_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={accessToken}";
    private static final String WEB_API_JS_API_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={accessToken}&type=jsapi";

    // 小程序相关, snake case
    private static final String WE_APP_API_SEND_SUBSCRIBE = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={accessToken}";
    private static final String WE_APP_API_JS_CODE_TO_SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code";

    // 其他接口
    private static final String API_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appId}&secret={secret}";

    private final TokenCacheService tokenCacheService;
    private final TicketCacheService ticketCacheService;
    private static RestTemplate SNAKE_CASE_OBJECT_REST_TEMPLATE = buildRestTemplate(PropertyNamingStrategy.SNAKE_CASE);
    private static RestTemplate CAMEL_CASE_OBJECT_REST_TEMPLATE = buildRestTemplate(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    private static ObjectMapper CAMEL_CASE_OBJECT_MAPPER = buildObjectMapper(PropertyNamingStrategy.LOWER_CAMEL_CASE);


    public WeChatClient(TokenCacheService tokenCacheService, TicketCacheService ticketCacheService) {
        this.tokenCacheService = tokenCacheService;
        this.ticketCacheService = ticketCacheService;
    }

    public <T> T decryptData(String encryptedData, String sessionKey, String iv, Class<T> dataType) {
        String json = WeChatAESInfoDecoder.decrypt(encryptedData, sessionKey, iv);
        T model;
        try {
            model = CAMEL_CASE_OBJECT_MAPPER.readValue(json, dataType);
        } catch (Exception e) {
            throw new RuntimeException("解析微信加密信息异常", e);
        }

        return model;
    }

    public SessionKey code2Session(String code, String appId, String secret) {
        Map<String, String> uriVars = new HashMap<>();
        uriVars.put("appId", appId);
        uriVars.put("secret", secret);
        uriVars.put("code", code);

        SessionKey session = SNAKE_CASE_OBJECT_REST_TEMPLATE.getForObject(
                WE_APP_API_JS_CODE_TO_SESSION,
                SessionKey.class,
                uriVars);

        if (session == null || StringUtils.isEmpty(session.getSessionKey())) {
            throw new RuntimeException("获取微信[" + appId + "]SessionKey失败:" + (session == null ? "" : session.getErrmsg()));
        }

        return session;
    }

    // snake case
    protected String getApiAccessToken(String appId, String secret) {
        String token = tokenCacheService.getToken(appId);
        if (token == null) {
            AccessToken accessToken = SNAKE_CASE_OBJECT_REST_TEMPLATE.getForObject(
                    API_GET_ACCESS_TOKEN,
                    AccessToken.class,
                    appId,
                    secret);

            if (accessToken == null || StringUtils.isEmpty(accessToken.getAccessToken())) {
                throw new RuntimeException("获取微信[" + appId + "]AccessToken失败:" + (accessToken == null ? "" : accessToken.getErrmsg()));
            }

            token = accessToken.getAccessToken();
            tokenCacheService.writeToken(appId, token, accessToken.getExpiresIn());
        }

        return token;
    }

    // code 授权，不需要缓存
    public WebUserAccessToken getWebUserAccessToken(String code, String appId, String secret) {
        WebUserAccessToken webUserAccessToken = SNAKE_CASE_OBJECT_REST_TEMPLATE.getForObject(
                WEB_API_GET_USER_ACCESS_TOKEN,
                WebUserAccessToken.class,
                appId,
                secret,
                code);

        if (webUserAccessToken == null || StringUtils.isEmpty(webUserAccessToken.getAccessToken())) {
            throw new RuntimeException("获取微信[" + appId + "]WebUserAccessToken失败:" + (webUserAccessToken == null ? "" : webUserAccessToken.getErrmsg()));
        }

        return webUserAccessToken;
    }

    public WebUserInfo getWebUserInfo(String accessToken, String openId) {
        WebUserInfo webUserInfo = SNAKE_CASE_OBJECT_REST_TEMPLATE.getForObject(
                WEB_API_GET_USER_INFO,
                WebUserInfo.class,
                accessToken,
                openId);

        if (webUserInfo == null || StringUtils.isEmpty(webUserInfo.getOpenid())) {
            throw new RuntimeException("获取微信WebUserInfo失败:" + (webUserInfo == null ? "" : webUserInfo.getErrmsg()));
        }

        return webUserInfo;
    }

    // snake case
    protected String getJsApiTicket(String appId, String appSecret) {
        String ticket = ticketCacheService.getTicket(appId);
        if (ticket == null) {
            JsApiTicket jsApiTicket = SNAKE_CASE_OBJECT_REST_TEMPLATE.getForObject(
                    WEB_API_JS_API_TICKET,
                    JsApiTicket.class,
                    getApiAccessToken(appId, appSecret)
            );

            if (jsApiTicket == null) {
                throw new RuntimeException("获取微信[" + appId + "]JsApiTicket内容为空");
            }

            if (jsApiTicket.getErrcode() != 0) {
                throw new RuntimeException("获取微信[" + appId + "]JsApiTicket失败:" + jsApiTicket.getErrmsg());
            }

            if (StringUtils.isEmpty(jsApiTicket.getTicket())) {
                throw new RuntimeException("获取微信[" + appId + "]JsApiTicket为空");
            }

            ticket = jsApiTicket.getTicket();
            ticketCacheService.writeTicket(appId, ticket, jsApiTicket.getExpiresIn());
        }

        return ticket;
    }


    public String jsApiSign(String appId, String secret, String url, String nonceStr, String timestamp) {
        String ticket = getJsApiTicket(appId, secret);
        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("noncestr", nonceStr);
        paramsMap.put("jsapi_ticket", ticket);
        paramsMap.put("timestamp", timestamp);
        paramsMap.put("url", url);

        String string1 = paramsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return DigestUtils.sha1Hex(string1);
    }

    public WebTemplateMessageSendResponse sendWebTemplateMessage(String appId, String secret, WebTemplateMessage templateMessage) {
        WebTemplateMessageSendResponse resp = SNAKE_CASE_OBJECT_REST_TEMPLATE.postForObject(
                WEB_API_SEND_TEMPLATE,
                templateMessage,
                WebTemplateMessageSendResponse.class,
                getApiAccessToken(appId, secret)
        );

        if (resp == null) {
            throw new RuntimeException("发送微信模板消息未返回数据");
        }

        if (resp.getErrcode() != 0) {
            throw new RuntimeException("发送微信模板消息失败:" + resp.getErrmsg());
        }

        return resp;
    }

    public SubscribeMessageSendResponse sendSubscribeMessage(String appId, String secret, SubscribeMessage subscribeMessage) {
        SubscribeMessageSendResponse response = SNAKE_CASE_OBJECT_REST_TEMPLATE.postForObject(
                WE_APP_API_SEND_SUBSCRIBE,
                subscribeMessage,
                SubscribeMessageSendResponse.class,
                getApiAccessToken(appId, secret)
        );
        if (response == null) {
            throw new RuntimeException("发送微信订阅模板消息未返回数据");
        }

        if (response.getErrcode() != 0) {
            throw new RuntimeException("发送微信订阅模板消息失败:" + response.getErrmsg());
        }

        return response;
    }

    private static RestTemplate buildRestTemplate(PropertyNamingStrategy propertyNamingStrategy) {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        factory.setConnectTimeout(60000);

        ObjectMapper objectMapper = buildObjectMapper(propertyNamingStrategy);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        messageConverters.add(new TextJackson2HttpMessageConverter(objectMapper));

        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

    private static ObjectMapper buildObjectMapper(PropertyNamingStrategy propertyNamingStrategy) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(propertyNamingStrategy);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
