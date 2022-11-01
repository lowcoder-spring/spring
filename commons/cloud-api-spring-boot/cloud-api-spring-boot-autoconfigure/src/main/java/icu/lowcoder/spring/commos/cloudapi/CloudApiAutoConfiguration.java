package icu.lowcoder.spring.commos.cloudapi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CloudApiProperties.class)
public class CloudApiAutoConfiguration {

    @Bean("cloudApiRestTemplate")
    public RestTemplate restTemplate(CloudApiProperties cloudApiProperties) {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(cloudApiProperties.getReadTimeout());
        factory.setConnectTimeout(cloudApiProperties.getConnectTimeout());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));

        restTemplate.setMessageConverters(messageConverters);
        if (!cloudApiProperties.getThrowOnResponseIsNotSuccessful()) {
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) {
                    return false;
                }
                @Override
                public void handleError(ClientHttpResponse response) {
                }
            });
        }

        return restTemplate;
    }

    @Primary
    @Bean(name = {"cloudApiDelegate", "bankCardApi", "realNameApi"})
    CloudApiDelegate cloudApiDelegate(CloudApiProperties cloudApiProperties, List<CloudApi> cloudApis) {
        return new CloudApiDelegate(cloudApiProperties, cloudApis);
    }

}
