package icu.lowcoder.spring.commons.exception.oauth2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = CustomizedOAuth2ExceptionSerializer.class)
public class CustomizedOAuth2Exception extends OAuth2Exception {

    private Map<String, Object> customAdditionalInformation = new HashMap<>();

    private String oAuth2ErrorCode = "invalid_request";

    public CustomizedOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }
    public CustomizedOAuth2Exception(String msg) {
        super(msg);
    }
    public CustomizedOAuth2Exception(String oAuth2ErrorCode, String message) {
        super(message);
        this.oAuth2ErrorCode = oAuth2ErrorCode;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return oAuth2ErrorCode;
    }

    public void setOAuth2ErrorCode(String oAuth2ErrorCode) {
        this.oAuth2ErrorCode = oAuth2ErrorCode;
    }

    public Map<String, Object> getCustomAdditionalInformation() {
        return customAdditionalInformation;
    }

    public void setCustomAdditionalInformation(Map<String, Object> customAdditionalInformation) {
        this.customAdditionalInformation = customAdditionalInformation;
    }
}
