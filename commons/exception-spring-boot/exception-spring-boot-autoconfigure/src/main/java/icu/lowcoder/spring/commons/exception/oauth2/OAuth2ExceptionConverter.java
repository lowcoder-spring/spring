package icu.lowcoder.spring.commons.exception.oauth2;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class OAuth2ExceptionConverter implements UnifiedExceptionConverter<OAuth2Exception> {
    @Override
    public UnifiedExceptionResponse convert(OAuth2Exception e) {
        String message = "认证错误";
        int statusCode = 400;
        switch (e.getOAuth2ErrorCode()) {
            case OAuth2Exception.INVALID_REQUEST:
                message = "错误的请求";
                break;
            case OAuth2Exception.INVALID_CLIENT:
            case OAuth2Exception.UNAUTHORIZED_CLIENT:
                message = "未知的客户端";
                statusCode = HttpStatus.FORBIDDEN.value();
                break;
            case OAuth2Exception.INVALID_GRANT:
                message = e.getMessage();
                break;
            case OAuth2Exception.INVALID_SCOPE:
                message = "认证失败";
                break;
            case OAuth2Exception.ACCESS_DENIED:
                statusCode = HttpStatus.FORBIDDEN.value();
                message = "拒绝访问";
                break;
            case OAuth2Exception.UNSUPPORTED_GRANT_TYPE:
                message = "未知授权类型";
                break;
            case OAuth2Exception.INVALID_TOKEN:
                statusCode = HttpStatus.UNAUTHORIZED.value();
                message = "无效的认证";
                break;
            case "unauthorized":
                statusCode = HttpStatus.UNAUTHORIZED.value();
                message = "未认证";
                break;
            case "method_not_allowed":
                statusCode = HttpStatus.METHOD_NOT_ALLOWED.value();
                message = "不允许的方法";
                break;
            case "server_error":
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
                message = "未知错误";
                break;
        }

        return UnifiedExceptionResponse.builder()
                .status(statusCode)
                .message(message)
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return OAuth2Exception.class.isAssignableFrom(clazz);
    }
}
