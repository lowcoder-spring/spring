package icu.lowcoder.spring.commons.security.expression;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class RequestSecurityExpressionUtils {
    public static boolean hasNotHeader(HttpServletRequest request, String headerName) {
        return !hasHeader(request, headerName);
    }

    public static boolean hasHeader(HttpServletRequest request, String headerName) {
        return Collections.list(request.getHeaderNames()).contains(headerName.toLowerCase());
    }

    public static boolean header(HttpServletRequest request, String headerName, String headerValue) {
        if (hasNotHeader(request, headerName)) {
            return false;
        }
        return request.getHeader(headerName.toLowerCase()).equals(headerValue);
    }
}
