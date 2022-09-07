package icu.lowcoder.spring.cloud.authentication.service;

import icu.lowcoder.spring.cloud.authentication.config.AuthProperties;
import icu.lowcoder.spring.cloud.authentication.dict.SmsCodeCategory;
import icu.lowcoder.spring.commons.sms.SmsSender;
import icu.lowcoder.spring.commons.sms.SmsType;
import icu.lowcoder.spring.commons.util.spring.SpringContextHolder;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SmsCodeManager {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AuthProperties authProperties;
    @Autowired
    private SmsSender smsSender;

    private static final String SMS_CACHE_KEY_PREFIX = "icu:lowcoder:auth:sms:";
    private static final String SMS_SEND_LIMIT_KEY_PREFIX = "icu:lowcoder:auth:sms-limit:";
    private static final long DEFAULT_SMS_SEND_LIMIT_MINUTE = 1; // 发送间隔
    private static final long DEFAULT_SMS_CACHE_MINUTE = 5; // 有效时长（5分钟）
    private static final int RANDOM_CODE_COUNT = 6;

    public void send(SmsCodeCategory category, String phone) {
        send(category, phone, DEFAULT_SMS_SEND_LIMIT_MINUTE, DEFAULT_SMS_CACHE_MINUTE);
    }
    public void send(SmsCodeCategory category, String phone, long sendLimitMinute, long expireMinute) {
        if (StringUtils.isBlank(phone)) {
            throw new RuntimeException("手机号码不能为空");
        }
        if (category == null) {
            throw new RuntimeException("必须指定短信验证码类型");
        }

        // 频繁发送
        String limitKey = buildCacheKey(SMS_SEND_LIMIT_KEY_PREFIX, category, phone);
        String cacheKey = buildCacheKey(SMS_CACHE_KEY_PREFIX, category, phone);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new RuntimeException("请勿频繁获取验证码");
        }

        String randomCode = RandomStringUtils.randomNumeric(RANDOM_CODE_COUNT);

        // 发送
        if (SpringContextHolder.isDevMode()) {
            // 开发用短信，跳过验证 123456
            randomCode = "123456";
        } else {
            String smsContent = null;
            switch (category) {
                case LOGIN -> smsContent = authProperties.getLoginSmsTemplate()
                        .replaceAll("#code#", randomCode)
                        .replaceAll("#effective#", expireMinute + "");
                case REGISTER -> smsContent = authProperties.getRegisterSmsTemplate()
                        .replaceAll("#code#", randomCode)
                        .replaceAll("#effective#", expireMinute + "");
            }
            if (smsContent == null) {
                throw new RuntimeException("无法构建短信内容, category:" + category.name());
            }

            smsSender.send(phone, smsContent, SmsType.VERIFICATION_CODE);
        }

        // 缓存
        redisTemplate.opsForValue().set(cacheKey, randomCode, expireMinute, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(limitKey, "L", sendLimitMinute, TimeUnit.MINUTES);
    }

    public void verify(SmsCodeCategory category, String phone, String code) {
        if (StringUtils.isBlank(phone)) {
            throw new RuntimeException("手机号码不能为空");
        }
        if (category == null) {
            throw new RuntimeException("必须指定短信验证码类型");
        }

        String cacheKey = buildCacheKey(SMS_CACHE_KEY_PREFIX, category, phone);
        // 是否存在
        if (Boolean.FALSE.equals(redisTemplate.hasKey(cacheKey))) {
            throw new RuntimeException("验证码已过期");
        }

        // 匹配
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if (!code.equals(cacheCode)) {
            throw new RuntimeException("验证码不匹配");
        }

        // 成功匹配后清除
        redisTemplate.delete(cacheKey);
    }

    private String buildCacheKey(String prefix, SmsCodeCategory category, String phone) {
        return prefix + category + "#" + phone;
    }
}
