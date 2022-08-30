package icu.lowcoder.spring.commons.feign;

import feign.Feign;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import icu.lowcoder.spring.commons.feign.encoder.PageableQueryEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated
//@Configuration
//@ConditionalOnClass(Feign.class)
//@EnableConfigurationProperties({FeignClientProperties.class, FeignHttpClientProperties.class})
public class FeignPageableEncoderAutoConfiguration {
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired
    public FeignPageableEncoderAutoConfiguration(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new PageableQueryEncoder(new SpringEncoder(messageConverters)));
    }
}
