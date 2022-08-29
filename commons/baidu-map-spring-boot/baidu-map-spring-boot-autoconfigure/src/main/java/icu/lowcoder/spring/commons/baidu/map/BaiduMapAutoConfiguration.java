package icu.lowcoder.spring.commons.baidu.map;

import icu.lowcoder.spring.commons.baidu.map.http.TextJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.baidu.map", name = {"ak"})
@EnableConfigurationProperties(BaiduMapProperties.class)
public class BaiduMapAutoConfiguration {

    @Bean
    public BaiduMapClient baiduMapClient(BaiduMapProperties baiduMapProperties) {
        BaiduMapClient client = new BaiduMapClient(baiduMapProperties);
        client.setRestTemplate(buildRestTemplate());

        return client;
    }

    private RestTemplate buildRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(60000);
        factory.setConnectTimeout(60000);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        messageConverters.add(new TextJackson2HttpMessageConverter(objectMapper));

        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }
}
