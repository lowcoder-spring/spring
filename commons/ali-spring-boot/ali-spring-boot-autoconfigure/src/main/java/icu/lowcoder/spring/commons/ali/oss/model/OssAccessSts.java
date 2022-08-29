package icu.lowcoder.spring.commons.ali.oss.model;

import lombok.Data;

@Data
public class OssAccessSts {
    private String securityToken;
    private String accessKeySecret;
    private String accessKeyId;
    private String expiration;
    // oss 配置
    private String bucket;
    private String endpoint;
}
