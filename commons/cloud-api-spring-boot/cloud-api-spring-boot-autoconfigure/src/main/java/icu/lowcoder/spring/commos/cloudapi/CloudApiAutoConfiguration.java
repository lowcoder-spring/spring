package icu.lowcoder.spring.commos.cloudapi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.cloudapi", name = "apis")
@EnableConfigurationProperties(CloudApiProperties.class)
public class CloudApiAutoConfiguration {

    @Bean("cloudApiRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        factory.setConnectTimeout(60000);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));

        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

    @Configuration(proxyBeanMethods = false)
    public static class CloudApiDelegateConfig {

        @Bean("cloudApiDelegate")
        CloudApiDelegate cloudApiDelegate(CloudApiProperties cloudApiProperties, List<CloudApi> cloudApis) {
            return new CloudApiDelegate(cloudApiProperties, cloudApis);
        }

        @Bean
        @Primary
        BankCardApi bankCardApi(CloudApiDelegate cloudApiDelegate) {
            return cloudApiDelegate;
        }

        @Bean
        @Primary
        RealNameApi realNameApi(CloudApiDelegate cloudApiDelegate) {
            return cloudApiDelegate;
        }
    }
}
