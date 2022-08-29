package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionKey extends BaseResponse {
    private String openid;
    private String sessionKey;
    private String unionid;
}
