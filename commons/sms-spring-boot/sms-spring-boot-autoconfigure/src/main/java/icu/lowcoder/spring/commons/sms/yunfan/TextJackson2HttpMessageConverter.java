package icu.lowcoder.spring.commons.sms.yunfan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class TextJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    public TextJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    public TextJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON,
                new MediaType("application", "*+json"),
                new MediaType("text", "javascript"),
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_HTML
        );
    }
}
