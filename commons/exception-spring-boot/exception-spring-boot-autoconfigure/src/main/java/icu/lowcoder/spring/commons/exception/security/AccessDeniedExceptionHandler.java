package icu.lowcoder.spring.commons.exception.security;

import icu.lowcoder.spring.commons.exception.ExceptionResponseBuilder;
import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.util.json.JsonUtils;
import icu.lowcoder.spring.commons.util.spring.SpringContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ExceptionResponseBuilder exceptionResponseBuilder = getExceptionResponseBuilder();
        if (exceptionResponseBuilder != null) {
            UnifiedExceptionResponse unifiedExceptionResponse = exceptionResponseBuilder.build(accessDeniedException, request.getRequestURI());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(unifiedExceptionResponse.getStatus());
            response.getWriter().write(JsonUtils.toJson(unifiedExceptionResponse));
            response.flushBuffer();
        } else {
            throw accessDeniedException;
        }
    }

    private ExceptionResponseBuilder getExceptionResponseBuilder() {
        try {
            return SpringContextHolder.applicationContext.getBean(ExceptionResponseBuilder.class);
        } catch (Exception e) {
            return null;
        }
    }
}
