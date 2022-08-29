package icu.lowcoder.spring.commons.robot.tencent;

import icu.lowcoder.spring.commons.robot.RobotVerifier;
import icu.lowcoder.spring.commons.robot.http.TextJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TencentCaptchaRobotVerifierService implements RobotVerifier {
    public static final String NAME = "Tencent";

    private static RestTemplate REST_TEMPLATE = null;

    private RestTemplate getRestTemplate() {
        if (REST_TEMPLATE == null) {
            RestTemplate restTemplate = new RestTemplate();

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setReadTimeout(60000);
            factory.setConnectTimeout(60000);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
            messageConverters.add(new TextJackson2HttpMessageConverter(objectMapper));

            restTemplate.setMessageConverters(messageConverters);
            REST_TEMPLATE = restTemplate;
        }

        return REST_TEMPLATE;
    }

    public TencentCaptchaRobotVerifierService(String appId, String appSecretKey) {
        this.appId = appId;
        this.appSecretKey = appSecretKey;
    }

    public void setVerifyUrl(String url) {
        this.verifyUrl = url;
    }

    private String verifyUrl = "https://ssl.captcha.qq.com/ticket/verify?aid={appId}&AppSecretKey={appSecretKey}&Ticket={ticket}&Randstr={randStr}&UserIP={userIP}";
    private final String appId;
    private final String appSecretKey;

    @Override
    public boolean allow(HttpServletRequest request, Map<String, String> params) {
        String randStr = params.get("ext_params_a");
        String ticket = params.get("ext_params_b");
        if (StringUtils.hasText(randStr) && StringUtils.hasText(ticket)) {
            // 去腾讯验证
            VerifyResponse resp = verify(getUserIp(request), randStr, ticket);
            if (resp != null && resp.getResponse() == 1) {
                return true;
            } else {
                if (resp == null) {
                    log.info("Tencent captcha verify fail, response null.");
                } else {
                    log.info("Tencent captcha verify fail, code:{}, msg:{}, level:{}", resp.getResponse(), resp.getErrMsg(), resp.getEvilLevel());
                }
            }
        }

        return false;
    }

    private VerifyResponse verify(String ip, String randStr, String ticket) {
        Map<String, Object> query = new HashMap<>();
        query.put("appId", appId);
        query.put("appSecretKey", appSecretKey);
        query.put("ticket", ticket);
        query.put("randStr", randStr);
        query.put("userIP", ip);

        return getRestTemplate().getForObject(verifyUrl, VerifyResponse.class, query);
    }

    private String getUserIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
