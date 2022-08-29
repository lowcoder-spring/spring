package icu.lowcoder.spring.commons.ali.oss.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UploadPolicy {
    private String accessId;
    private String policy;
    private String signature;
    private String dir;
    private String host;
    private Long expire;
    private String callback;

    private Map<String, String> customParams = new HashMap<>();
}
