package icu.lowcoder.spring.commons.logging.access.handler;

import icu.lowcoder.spring.commons.exception.ExceptionResponseBuilder;
import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.logging.access.AccessLog;
import icu.lowcoder.spring.commons.logging.access.AccessLoggingProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class AccessLogPrinter {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

    private final AccessLoggingProperties accessLoggingProperties;
    private ObjectMapper objectMapper = DEFAULT_OBJECT_MAPPER;
    private final StringPrincipalExtractor principalExtractor;

    public AccessLogPrinter(AccessLoggingProperties accessLoggingProperties, ObjectMapper objectMapper,
                            StringPrincipalExtractor principalExtractor) {
        this.accessLoggingProperties = accessLoggingProperties;
        this.objectMapper = objectMapper;
        this.principalExtractor = principalExtractor;
    }
    public AccessLogPrinter(AccessLoggingProperties accessLoggingProperties,
                            StringPrincipalExtractor principalExtractor) {
        this.accessLoggingProperties = accessLoggingProperties;
        this.principalExtractor = principalExtractor;
    }

    protected AccessLog buildLog(HttpServletRequest request, HttpServletResponse response) {
        AccessLog accessLog = new AccessLog();
        accessLog.setRemoteAddr(request.getRemoteAddr());
        accessLog.setRemoteHost(request.getRemoteHost());
        accessLog.setPrincipal(principalExtractor.extract());

        accessLog.setHttpMethod(request.getMethod());
        accessLog.setProtocol(request.getProtocol());
        accessLog.setRequestUri(request.getRequestURI());
        accessLog.setQueryString(request.getQueryString());

        // params
        if (accessLoggingProperties.getIgnoreParams() != null && !accessLoggingProperties.getIgnoreParams().isEmpty()) {
            accessLog.setParams(extractParams(request, accessLoggingProperties.getIgnoreParams(), accessLoggingProperties.getIgnoreParamsReplacement()));
        }
        // request header
        if (accessLoggingProperties.getRecordRequestHeaders() != null && !accessLoggingProperties.getRecordRequestHeaders().isEmpty()) {
            accessLog.setRequestHeaders(extractRequestHeaders(request, accessLoggingProperties.getRecordRequestHeaders()));
        }
        // request body
        if (accessLoggingProperties.getEnabledRecordRequestBody() && accessLoggingProperties.getRecordBodyTypes() != null && !accessLoggingProperties.getRecordBodyTypes().isEmpty()) {
            accessLog.setRequestBody(convertRequestBody(request, accessLoggingProperties.getRecordBodyTypes(), accessLoggingProperties.getMaxPayloadLength(), accessLoggingProperties.getDeserializeBody()));
        }

        accessLog.setStatusCode(response.getStatus());
        // response header
        if (accessLoggingProperties.getRecordResponseHeaders() != null && !accessLoggingProperties.getRecordResponseHeaders().isEmpty()) {
            accessLog.setResponseHeaders(extractResponseHeaders(response, accessLoggingProperties.getRecordResponseHeaders()));
        }
        // response body
        if (accessLoggingProperties.getEnabledRecordResponseBody() && accessLoggingProperties.getRecordBodyTypes() != null && !accessLoggingProperties.getRecordBodyTypes().isEmpty()) {
            Object responseBody = null;

            Object exception = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
            if (exception != null) { // 异常
                WebApplicationContext webApplicationContext = WebApplicationContextUtils.findWebApplicationContext(request.getServletContext());
                if (webApplicationContext != null) {
                    try {
                        ExceptionResponseBuilder exceptionResponseBuilder = webApplicationContext.getBean(ExceptionResponseBuilder.class);
                        UnifiedExceptionResponse exceptionResponse = exceptionResponseBuilder.build((Throwable) exception, request.getRequestURI());
                        String respJson = objectMapper.writeValueAsString(exceptionResponse);
                        responseBody = convertByteContent(
                                respJson.getBytes(StandardCharsets.UTF_8),
                                StandardCharsets.UTF_8.name(),
                                accessLoggingProperties.getMaxPayloadLength(),
                                accessLoggingProperties.getDeserializeBody()
                        );
                    } catch (Exception ignored) {
                    }
                }
            }

            if (responseBody == null) {
                responseBody = convertResponseBody(response, accessLoggingProperties.getRecordBodyTypes(),
                        accessLoggingProperties.getMaxPayloadLength(), accessLoggingProperties.getDeserializeBody());
            }

            accessLog.setResponseBody(responseBody);
        }

        return accessLog;
    }

    private Object convertResponseBody(HttpServletResponse response, List<String> recordBodyTypes, long contentLengthLimit, boolean deserializeBody) {
        boolean shouldRecord = contentTypeMatch(response.getContentType(), recordBodyTypes);

        ContentCachingResponseWrapper cachingResponseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);

        if (shouldRecord && cachingResponseWrapper != null) {
            return convertByteContent(cachingResponseWrapper.getContentAsByteArray(), cachingResponseWrapper.getCharacterEncoding(), contentLengthLimit, deserializeBody);
        } else {
            return Collections.singletonMap("_body_convert_msg", "no deserialized, no matched type: " + response.getContentType());
        }
    }

    private Object convertRequestBody(HttpServletRequest request, List<String> recordBodyTypes, long contentLengthLimit, boolean deserializeBody) {
        boolean shouldRecord = contentTypeMatch(request.getContentType(), recordBodyTypes);

        ContentCachingRequestWrapper cachingRequestWrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

        if (shouldRecord && cachingRequestWrapper != null) {
            return convertByteContent(cachingRequestWrapper.getContentAsByteArray(), cachingRequestWrapper.getCharacterEncoding(), contentLengthLimit, deserializeBody);
        } else {
            return Collections.singletonMap("_body_convert_msg", "no deserialized, no matched type: " + request.getContentType());
        }
    }

    private Object convertByteContent(byte[] content, String contentEncoding, long contentLengthLimit, boolean deserializeBody) {
        if (content.length > contentLengthLimit) {
            return Collections.singletonMap("_body_convert_msg", "** content length " + content.length + " has exceeded " + contentLengthLimit);
        }

        try {
            String contentStr = new String(content, contentEncoding);
            if (deserializeBody) {
                // json object
                if (contentStr.startsWith("{") && contentStr.endsWith("}")) {
                    try {
                        return objectMapper.readValue(contentStr, Map.class);
                    } catch (JsonProcessingException e) {
                        return contentStr;
                    }
                } else if (contentStr.startsWith("[") && contentStr.endsWith("]")) {
                    // json array
                    try {
                        return objectMapper.readValue(contentStr, Collection.class);
                    } catch (JsonProcessingException e) {
                        return contentStr;
                    }
                } else {
                    // other
                    Map<String, String> body = new HashMap<>();
                    body.put("_body_convert_msg", "no supported content type, content length: " + content.length);
                    body.put("_raw", contentStr);
                    return body;
                }
            } else {
                Map<String, String> body = new HashMap<>();
                body.put("_body_convert_msg", "no deserialized, content length: " + content.length);
                body.put("_raw", contentStr);
                return body;
            }
        } catch (UnsupportedEncodingException e) {
            return Collections.singletonMap("_body_convert_msg", "** unsupported encoding: " + contentEncoding + ", content length: " + content.length);
        }
    }

    private boolean contentTypeMatch(String contentType, List<String> recordBodyTypes) {
        boolean matched = false;
        if (contentType == null) {
            return false;
        }
        try {
            MediaType requestType = MediaType.valueOf(contentType);
            for (String type : recordBodyTypes) {
                if (MediaType.valueOf(type).isCompatibleWith(requestType)) {
                    matched = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("Can't match content type: {}", contentType, e);
        }

        return matched;
    }

    private Map<String, String> extractResponseHeaders(HttpServletResponse response, List<String> recordResponseHeaders) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String name : headerNames) {
            if (recordResponseHeaders.contains(name.toLowerCase())) {
                headers.put(name, response.getHeader(name));
            }
        }

        return headers;
    }

    private Map<String, String> extractRequestHeaders(HttpServletRequest request, List<String> recordRequestHeaders) {
        Map<String, String> headers = new HashMap<>();
        @SuppressWarnings("rawtypes")
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = String.valueOf(headerNames.nextElement());
            if (recordRequestHeaders.contains(name.toLowerCase())) {
                headers.put(name, request.getHeader(name));
            }
        }

        return headers;
    }

    private Map<String, String> extractParams(HttpServletRequest request, List<String> ignoreParams, String ignoreParamsReplacement) {
        Map<String, String> params = new HashMap<>();
        @SuppressWarnings("rawtypes")
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = String.valueOf(paramNames.nextElement());
            params.put(name, ignoreParams.contains(name)  ? ignoreParamsReplacement : request.getParameter(name));
        }
        return params;
    }

    public void print(HttpServletRequest request, HttpServletResponse response, Date timestamp, long elapsedTime) {
        try {
            AccessLog accessLog = buildLog(request, response);
            accessLog.setElapsedTime(elapsedTime);
            accessLog.setTimestamp(timestamp);

            log.info(Markers.append("access", accessLog), "{} {} {} {}ms", accessLog.getHttpMethod(), accessLog.getRequestUri(), accessLog.getStatusCode(), accessLog.getElapsedTime());
        } catch (Exception e) {
            log.warn("Record access log exception.", e);
        }
    }

}
