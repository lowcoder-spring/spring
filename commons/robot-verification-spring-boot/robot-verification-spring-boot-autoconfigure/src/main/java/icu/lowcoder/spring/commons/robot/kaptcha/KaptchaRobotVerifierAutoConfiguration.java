package icu.lowcoder.spring.commons.robot.kaptcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import icu.lowcoder.spring.commons.robot.RobotVerificationAutoConfiguration;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.robot-verification.kaptcha", name = {"img-endpoint"})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(KaptchaRobotVerifierProperties.class)
@AutoConfigureBefore(RobotVerificationAutoConfiguration.class)
public class KaptchaRobotVerifierAutoConfiguration {

    @Bean
    DefaultKaptcha kaptcha(KaptchaRobotVerifierProperties verifierProperties) {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        Properties defProperties = new Properties();
        defProperties.setProperty("kaptcha.border", "no");
        //defProperties.setProperty("kaptcha.border.color", "105,179,90");
        //defProperties.setProperty("kaptcha.textproducer.font.color", "blue");
        defProperties.setProperty("kaptcha.image.width", "100");
        defProperties.setProperty("kaptcha.image.height", "38");
        defProperties.setProperty("kaptcha.session.key", "ICU_LOWCODER_SPRING_COMMONS_ROBOT_VERIFICATION_KAPTCHA");
        defProperties.setProperty("kaptcha.textproducer.char.length", "4");
        //properties.setProperty("kaptcha.textproducer.font.names", "Courier");
        defProperties.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        defProperties.setProperty("kaptcha.textproducer.font.size", "30");
        defProperties.setProperty("kaptcha.textproducer.char.space", "3");
        //defProperties.setProperty("kaptcha.background.clear.from", "white");
        //defProperties.setProperty("kaptcha.background.clear.to", "white");
        //defProperties.setProperty("kaptcha.obscurificator.impl", "icu.lowcoder.spring.commons.robot.kaptcha.NoGimpy");
        //defProperties.setProperty("kaptcha.noise.color", "190,190,190");
        //defProperties.setProperty("kaptcha.noise.impl", "icu.lowcoder.spring.commons.robot.kaptcha.DotNoise");

        verifierProperties.getProperties().forEach((k, v) -> defProperties.merge(k, v, (defValue, value) -> value));

        Config config = new Config(defProperties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

    @Bean("kaptchaRobotVerifier")
    KaptchaRobotVerifier kaptchaRobotVerifier(
            KaptchaRobotVerifierProperties verifierProperties,
            DefaultKaptcha kaptcha,
            @Autowired(required = false) RedisTemplate<String, String> redisTemplate
    ) {
        KaptchaRobotVerifier verifier = null;
        switch (verifierProperties.getStrategy()) {
            case SESSION ->
                    verifier = KaptchaRobotVerifier.SessionStrategy(kaptcha, verifierProperties.getReqParamName());
            case REDIS -> {
                if (redisTemplate == null) {
                    throw new RuntimeException("REDIS strategy required redis configuration.");
                }
                verifier = KaptchaRobotVerifier.RedisStrategy(kaptcha, verifierProperties.getReqParamName(),
                        redisTemplate,
                        verifierProperties.getRequestIdHeader(),
                        verifierProperties.getRedisKey()
                );
            }
        }

        return verifier;
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            KaptchaRobotVerifierProperties verifierProperties,
            DefaultKaptcha kaptcha,
            RedisTemplate<String, String> redisTemplate) {

        KaptchaImgHandler kaptchaImgHandler = new KaptchaImgHandler(kaptcha, verifierProperties, redisTemplate);
        return route()
                .GET(verifierProperties.getImgEndpoint(), kaptchaImgHandler::getImage)
                .build();
    }

    static class KaptchaImgHandler {

        private final DefaultKaptcha kaptcha;
        private final KaptchaRobotVerifierProperties kaptchaRobotVerifierProperties;
        private final RedisTemplate<String, String> redisTemplate;

        public KaptchaImgHandler(DefaultKaptcha kaptcha, KaptchaRobotVerifierProperties kaptchaRobotVerifierProperties, RedisTemplate<String, String> redisTemplate) {
            this.kaptcha = kaptcha;
            this.kaptchaRobotVerifierProperties = kaptchaRobotVerifierProperties;
            this.redisTemplate = redisTemplate;
        }

        @SneakyThrows
        public ServerResponse getImage(ServerRequest request) {
            File imgFile = File.createTempFile(UUID.randomUUID().toString(), ".kaptcha");

            String capText = kaptcha.createText();
            String requestId = System.currentTimeMillis() + String.valueOf((int) (Math.random() * 10000));
            // storage
            switch (kaptchaRobotVerifierProperties.getStrategy()) {
                case SESSION -> request.session().setAttribute(kaptcha.getConfig().getSessionKey(), capText);
                case REDIS -> redisTemplate.opsForValue().set(kaptchaRobotVerifierProperties.getRedisKey() + "#" + requestId, capText, 5, TimeUnit.MINUTES);
            }

            BufferedImage bi = kaptcha.createImage(capText);
            ImageIO.write(bi, "jpeg", imgFile);

            return ServerResponse
                    .ok()
                    .header(kaptchaRobotVerifierProperties.getRequestIdHeader(), requestId)
                    .header(HttpHeaders.EXPIRES, "0")
                    .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, post-check=0, pre-check=0")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new FileSystemResource(imgFile));
        }
    }


}
