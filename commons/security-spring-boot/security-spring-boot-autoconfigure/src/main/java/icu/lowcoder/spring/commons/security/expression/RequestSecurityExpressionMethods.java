package icu.lowcoder.spring.commons.security.expression;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class RequestSecurityExpressionMethods {

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes)requestAttributes).getRequest();
        }

        return null;
    }

    public boolean hasNotHeader(String headerName) {
        return !hasHeader(headerName);
    }

    public boolean hasHeader(String headerName) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return false;
        }
        return Collections.list(request.getHeaderNames()).contains(headerName.toLowerCase());
    }

    public boolean header(String headerName, String headerValue) {
        if (hasNotHeader(headerName)) {
            return false;
        }
        return getRequest().getHeader(headerName.toLowerCase()).equals(headerValue);
    }

}
