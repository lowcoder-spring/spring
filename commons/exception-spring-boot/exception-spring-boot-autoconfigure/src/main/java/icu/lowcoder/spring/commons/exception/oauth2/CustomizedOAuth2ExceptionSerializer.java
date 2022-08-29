package icu.lowcoder.spring.commons.exception.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Map;

public class CustomizedOAuth2ExceptionSerializer extends JsonSerializer<CustomizedOAuth2Exception> {
    protected CustomizedOAuth2ExceptionSerializer() {
    }
    
    @Override
    public void serialize(CustomizedOAuth2Exception value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        //jsonGenerator.writeObjectField("status", value.getHttpErrorCode());
        /*String errorMessage = value.getOAuth2ErrorCode();
        if (errorMessage != null) {
            errorMessage = HtmlUtils.htmlEscape(errorMessage);
        }*/
        //jsonGenerator.writeStringField("msg", errorMessage);
        if (value.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
        }
        if (value.getCustomAdditionalInformation()!=null) {
            for (Map.Entry<String, Object> entry : value.getCustomAdditionalInformation().entrySet()) {
                jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
            }
        }
        jsonGenerator.writeEndObject();
    }
}
