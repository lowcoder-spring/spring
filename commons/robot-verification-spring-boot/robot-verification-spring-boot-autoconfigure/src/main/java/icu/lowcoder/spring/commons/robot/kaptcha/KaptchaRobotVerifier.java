package icu.lowcoder.spring.commons.robot.kaptcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import icu.lowcoder.spring.commons.robot.RobotVerifier;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class KaptchaRobotVerifier implements RobotVerifier {
    private DefaultKaptcha kaptcha;

    private String reqParamName;
    private KaptchaStrategy strategy;

    private String requestIdHeader;

    private String redisKey;

    private RedisTemplate<String, String> redisTemplate;

    private KaptchaRobotVerifier() {

    }

    public static KaptchaRobotVerifier SessionStrategy(DefaultKaptcha kaptcha, String reqParamName) {
        KaptchaRobotVerifier verifier = new KaptchaRobotVerifier();
        verifier.setKaptcha(kaptcha);
        verifier.setReqParamName(reqParamName);
        verifier.setStrategy(KaptchaStrategy.SESSION);
        return verifier;
    }

    public static KaptchaRobotVerifier RedisStrategy(
            DefaultKaptcha kaptcha, String reqParamName,
            RedisTemplate<String, String> redisTemplate,
            String requestIdHeader, String redisKey) {
        KaptchaRobotVerifier verifier = new KaptchaRobotVerifier();
        verifier.setKaptcha(kaptcha);
        verifier.setReqParamName(reqParamName);
        verifier.setStrategy(KaptchaStrategy.REDIS);
        verifier.setRedisTemplate(redisTemplate);
        verifier.setRequestIdHeader(requestIdHeader);
        verifier.setRedisKey(redisKey);
        return verifier;
    }

    @Override
    public boolean allow(HttpServletRequest request, Map<String, String> params) {
        log.debug("tester strategy: {}", strategy.name());
        String inStorage = null;
        switch (strategy) {
            case REDIS -> {
                String requestId = request.getHeader(this.requestIdHeader);
                log.debug("kaptcha request id: {}", requestId);
                if (!StringUtils.hasText(requestId)) {
                    return false;
                }
                inStorage = this.redisTemplate.opsForValue().get(this.redisKey + "#" + requestId);
            }
            case SESSION -> {
                Object sessionImageCode = request.getSession().getAttribute(kaptcha.getConfig().getSessionKey());
                if (sessionImageCode == null) {
                    return false;
                }
                inStorage = String.valueOf(sessionImageCode);
            }
        }

        log.debug("In storage value: {}", inStorage);
        if (inStorage == null) {
            return false;
        } else {
            String reqCaptcha = request.getParameter(reqParamName);
            log.debug("In request value: {}", reqCaptcha);
            if (reqCaptcha == null) {
                return false;
            }

            boolean allow = reqCaptcha.equalsIgnoreCase(inStorage);
            if (allow) {
                switch (strategy) {
                    case REDIS -> {
                        String requestId = request.getHeader(this.requestIdHeader);
                        this.redisTemplate.delete(this.redisKey + "#" + requestId);
                    }
                    case SESSION -> {
                        request.getSession().removeAttribute(kaptcha.getConfig().getSessionKey());
                    }
                }

            }
            return allow;
        }
    }


    @Override
    public String getName() {
        return "Kaptcha";
    }
}
