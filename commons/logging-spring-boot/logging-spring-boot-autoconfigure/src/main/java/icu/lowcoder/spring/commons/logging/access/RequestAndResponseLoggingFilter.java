package icu.lowcoder.spring.commons.logging.access;

import icu.lowcoder.spring.commons.logging.access.handler.AccessLogPrinter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Spring Web filter for logging request and response.
 *
 * @see org.springframework.web.filter.AbstractRequestLoggingFilter
 * @see ContentCachingRequestWrapper
 * @see ContentCachingResponseWrapper
 * @author https://gist.github.com/int128/e47217bebdb4c402b2ffa7cc199307ba
 *
 */
@Slf4j
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    private final AntPathMatcher antPathMatcher= new AntPathMatcher();

    private final AccessLoggingProperties accessLoggingProperties;
    private final AccessLogPrinter accessLogPrinter;

    public RequestAndResponseLoggingFilter(AccessLoggingProperties accessLoggingProperties, AccessLogPrinter accessLogPrinter) {
        this.accessLogPrinter = accessLogPrinter;
        this.accessLoggingProperties = accessLoggingProperties;
    }

    protected boolean shouldLog(HttpServletRequest request) {
        List<String> excludedUrls = accessLoggingProperties.getExcludedUrls();
        if (excludedUrls == null || excludedUrls.isEmpty()) {
            return true;
        } else {
            for (String pattern : excludedUrls) {
                if (antPathMatcher.match(pattern, request.getRequestURI())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isAsyncDispatch(request) || !shouldLog(request)) {
            filterChain.doFilter(request, response);
        } else {
            ContentCachingRequestWrapper wrappedRequest = wrapRequest(request);
            ContentCachingResponseWrapper wrappedResponse = wrapResponse(response);

            try {
                beforeRequest(wrappedRequest);
                filterChain.doFilter(wrappedRequest, wrappedResponse);
            } finally {
                afterRequest(wrappedRequest, wrappedResponse);
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper request) {
        startTime.set(System.currentTimeMillis());
    }

    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        accessLogPrinter.print(request, response, new Date(startTime.get()), System.currentTimeMillis() - startTime.get());
        startTime.remove();
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

}
