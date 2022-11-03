package icu.lowcoder.spring.commons.sms.yunfan;

import icu.lowcoder.spring.commons.sms.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class YunFanSmsSender extends SmsSender {
    private static final Map<String, String> RESPONSE_CODE_MAP = new HashMap<>();
    static {
        RESPONSE_CODE_MAP.put("00000", "提交成功");
        RESPONSE_CODE_MAP.put("F0001", "参数appkey未填写");
        RESPONSE_CODE_MAP.put("F0002", "参数appcode未填写");
        RESPONSE_CODE_MAP.put("F0003", "参数phone未填写");
        RESPONSE_CODE_MAP.put("F0004", "参数sign未填写");
        RESPONSE_CODE_MAP.put("F0005", "参数timestamp未填写");
        RESPONSE_CODE_MAP.put("F0006", "appkey不存在");
        RESPONSE_CODE_MAP.put("F0007", "账号已经关闭");
        RESPONSE_CODE_MAP.put("F0008", "sign检验错误");
        RESPONSE_CODE_MAP.put("F0009", "账号下没有业务");
        RESPONSE_CODE_MAP.put("F0010", "业务不存在");
        RESPONSE_CODE_MAP.put("F0011", "手机号码超过1000个");
        RESPONSE_CODE_MAP.put("F0012", "timestamp不是数字");
        RESPONSE_CODE_MAP.put("F0013", "timestamp过期超过5分钟");
        RESPONSE_CODE_MAP.put("F0014", "请求ip不在白名单内");
        RESPONSE_CODE_MAP.put("F0015", "余额不足");
        RESPONSE_CODE_MAP.put("F0016", "手机号码无效");
        RESPONSE_CODE_MAP.put("F0017", "没有可用的业务");
        RESPONSE_CODE_MAP.put("F0022", "参数msg未填写");
        RESPONSE_CODE_MAP.put("F0023", "msg超过了1000个字");
        RESPONSE_CODE_MAP.put("F0024", "extend不是纯数字");
        RESPONSE_CODE_MAP.put("F0025", "内容签名未报备/无签名");
        RESPONSE_CODE_MAP.put("F0039", "参数sms未填写");
        RESPONSE_CODE_MAP.put("F0040", "参数sms格式不正确");
        RESPONSE_CODE_MAP.put("F0041", "短信条数超过1000条");
        RESPONSE_CODE_MAP.put("F0050", "无数据");
        RESPONSE_CODE_MAP.put("F0100", "未知错误");
    }

    private static RestTemplate REST_TEMPLATE = new RestTemplate();

    private final YunFanSmsProperties yunFanSmsProperties;
    private final SmsProperties smsProperties;

    public YunFanSmsSender(YunFanSmsProperties yunFanSmsProperties, SmsProperties smsProperties) {
        this.yunFanSmsProperties = yunFanSmsProperties;
        this.smsProperties = smsProperties;
    }


    @Override
    public void send(String target, String content, SmsType type) throws SmsSendException {
        if(!PhoneNumberUtils.isPhoneNumber(target)) {
            throw new SmsSendException("手机号码格式不正确");
        }

        send(Collections.singletonList(target), content);
    }

    @Override
    public void send(List<String> targets, String content) throws SmsSendException {
        if (targets.size() > smsProperties.getMaxBatchSize()) {
            throw new SmsSendException("超过批量发送最大数量限制：" + smsProperties.getMaxBatchSize());
        }

        Set<String> phonesSet = new HashSet<>();
        targets.forEach(p -> {
            if (PhoneNumberUtils.isPhoneNumber(p)) {
                phonesSet.add(p);
            }
        });

        if (phonesSet.size() == 0) {
            throw new SmsSendException("需要至少一个正确的电话号码");
        }

        ResponseEntity<YunFanSmsResponse> resp = REST_TEMPLATE.postForEntity(
                yunFanSmsProperties.getUrl(),
                buildRequest(StringUtils.collectionToCommaDelimitedString(phonesSet), content),
                YunFanSmsResponse.class);

        processResponse(resp);
    }

    @SuppressWarnings("rawtypes")
    private HttpEntity buildRequest(String phones, String finalContent) {
        YunFanSmsRequest request = new YunFanSmsRequest();
        request.setAppcode(yunFanSmsProperties.getAppCode());
        request.setAppkey(yunFanSmsProperties.getAppKey());

        String timestamp = System.currentTimeMillis() + "";
        String sign = DigestUtils.md5DigestAsHex((yunFanSmsProperties.getAppKey() + yunFanSmsProperties.getAppSecret() + timestamp).getBytes());
        request.setTimestamp(timestamp);
        request.setSign(sign);
        request.setPhone(phones);
        request.setMsg(finalContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }

    private void processResponse(ResponseEntity<YunFanSmsResponse> resp) {
        if (!resp.getStatusCode().is2xxSuccessful()) {
            YunFanSmsResponse yunFanSmsResponse = resp.getBody();
            if (yunFanSmsResponse == null) {
                throw new SmsSendException(RESPONSE_CODE_MAP.getOrDefault("F0100", "未知错误"));
            } else {
                throw new SmsSendException(yunFanSmsResponse.getDesc());
            }
        }
    }

}
