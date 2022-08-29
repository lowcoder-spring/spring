package icu.lowcoder.spring.commons.sms.lingkai;

import icu.lowcoder.spring.commons.sms.*;
import icu.lowcoder.spring.commons.sms.lingkai.http.IntegerHttpMessageConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;

public class LingKaiSmsSender extends SmsSender {
    private static final Map<Integer, String> RESPONSE_CODE_MAP = new HashMap<>();
    static {
        RESPONSE_CODE_MAP.put(-1, "帐号未注册");
        RESPONSE_CODE_MAP.put(-2, "其他错误");
        RESPONSE_CODE_MAP.put(-3, "密码错误");
        RESPONSE_CODE_MAP.put(-5, "余额不足");
        RESPONSE_CODE_MAP.put(-6, "定时发送时间不是有效的时间格式");
        RESPONSE_CODE_MAP.put(-7, "提交信息末尾未加签名，请添加中文企业签名");
        RESPONSE_CODE_MAP.put(-8, "发送内容需在1到500个字之间");
        RESPONSE_CODE_MAP.put(-9, "发送号码为空");
    }

    private final LingKaiProperties lingKaiProperties;
    private final SmsProperties smsProperties;

    public LingKaiSmsSender(LingKaiProperties lingKaiProperties, SmsProperties smsProperties) {
        this.lingKaiProperties = lingKaiProperties;
        this.smsProperties = smsProperties;
    }

    protected RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        FormHttpMessageConverter fc = new FormHttpMessageConverter();
        IntegerHttpMessageConverter integerConverter = new IntegerHttpMessageConverter(Charset.forName(lingKaiProperties.getEncoding()));
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName(lingKaiProperties.getEncoding()));
        List<HttpMessageConverter<?>> partConverters = new ArrayList<>();
        partConverters.add(stringConverter);
        partConverters.add(new ResourceHttpMessageConverter());
        fc.setPartConverters(partConverters);
        restTemplate.getMessageConverters().addAll(Arrays.asList(integerConverter, stringConverter, fc, new MappingJackson2HttpMessageConverter()));

        return restTemplate;
    }

    @Override
    public void send(String target, String content, SmsType type) throws SmsSendException {
        if(!PhoneNumberUtils.isPhoneNumber(target)) {
            throw new SmsSendException("手机号码格式不正确");
        }

        ResponseEntity<Integer> resp = getRestTemplate().postForEntity(
                lingKaiProperties.getUrl(),
                buildRequest(target, buildFinalContent(content, type)),
                Integer.class);

        processResponse(resp);
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

        ResponseEntity<Integer> resp = getRestTemplate().postForEntity(
                lingKaiProperties.getUrl(),
                buildRequest(StringUtils.collectionToCommaDelimitedString(phonesSet), buildFinalContent(content, SmsType.OTHER)),
                Integer.class);

        processResponse(resp);
    }

    @SuppressWarnings("rawtypes")
    private HttpEntity buildRequest(String phones, String finalContent) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("Mobile", phones);
        form.add("Content", finalContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/x-www-form-urlencoded;charset=" + lingKaiProperties.getEncoding()));
        return new HttpEntity<>(form, headers);
    }

    private void processResponse(ResponseEntity<Integer> resp) {
        if (!resp.getStatusCode().is2xxSuccessful()) {
            Integer code = resp.getBody();
            if (code == null) {
                throw new SmsSendException(RESPONSE_CODE_MAP.getOrDefault(code, "未知错误"));
            } else {
                throw new SmsSendException("短信网关返回非200且body为空");
            }
        }
    }

    private String buildFinalContent(String content, SmsType type) {
        String finalContent = content;

        if (type != SmsType.VERIFICATION_CODE) {
            finalContent += lingKaiProperties.getUnsubscribe();
        }
        finalContent += lingKaiProperties.getSign();

        return  finalContent;
    }
}
