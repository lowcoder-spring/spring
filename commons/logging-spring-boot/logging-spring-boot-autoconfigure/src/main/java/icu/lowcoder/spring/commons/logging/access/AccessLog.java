package icu.lowcoder.spring.commons.logging.access;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class AccessLog {
    private String remoteAddr;
    private String remoteHost;
    //private String client;
    private String principal;

    private Date timestamp = new Date();
    private String httpMethod;
    private String protocol;
    private String requestUri;
    private String queryString;
    private Map<String, String> params;
    private Map<String, String> requestHeaders;
    private Object requestBody;

    private Integer statusCode;
    private Long elapsedTime;
    private Map<String, String> responseHeaders;
    private Object responseBody;
}
