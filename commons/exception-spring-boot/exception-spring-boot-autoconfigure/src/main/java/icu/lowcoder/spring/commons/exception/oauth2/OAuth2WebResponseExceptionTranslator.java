package icu.lowcoder.spring.commons.exception.oauth2;

import icu.lowcoder.spring.commons.exception.ExceptionResponseBuilder;
import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @see DefaultWebResponseExceptionTranslator
 */
public class OAuth2WebResponseExceptionTranslator implements WebResponseExceptionTranslator<CustomizedOAuth2Exception> {

    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    private final ExceptionResponseBuilder exceptionResponseBuilder;

    public OAuth2WebResponseExceptionTranslator(ExceptionResponseBuilder exceptionResponseBuilder) {
        this.exceptionResponseBuilder = exceptionResponseBuilder;
    }

    @Override
    public ResponseEntity<CustomizedOAuth2Exception> translate(Exception e) {
        // Try to extract a SpringSecurityException from the stacktrace
        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);
        Exception ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);

        if (ase != null) {
            return handleOAuth2Exception(((OAuth2Exception)ase).getOAuth2ErrorCode(), e);
        }

        ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception("unauthorized", ase);
        }

        ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception("access_denied", ase);
        }

        ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception("method_not_allowed", ase);
        }

        return handleOAuth2Exception("server_error", e);
    }

    private ResponseEntity<CustomizedOAuth2Exception> handleOAuth2Exception(String oauth2ErrorCode, Exception e) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");

        String path = "/oauth";
        RequestAttributes currentRequestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) currentRequestAttributes).getRequest();
        path = request.getRequestURI();

        CustomizedOAuth2Exception customizedOAuth2Exception = new CustomizedOAuth2Exception(e.getMessage(), e);
        customizedOAuth2Exception.setOAuth2ErrorCode(oauth2ErrorCode);

        UnifiedExceptionResponse response = exceptionResponseBuilder.build(customizedOAuth2Exception, path);
        customizedOAuth2Exception.getCustomAdditionalInformation().put("timestamp", response.getTimestamp());
        customizedOAuth2Exception.getCustomAdditionalInformation().put("status", response.getStatus());
        customizedOAuth2Exception.getCustomAdditionalInformation().put("exception", e.getClass().getName());
        customizedOAuth2Exception.getCustomAdditionalInformation().put("message", response.getMessage());
        customizedOAuth2Exception.getCustomAdditionalInformation().put("uri", response.getUri());
        customizedOAuth2Exception.getCustomAdditionalInformation().put("detail", response.getDetail());

        return new ResponseEntity<>(customizedOAuth2Exception, headers, HttpStatus.valueOf(response.getStatus()));
    }
}
