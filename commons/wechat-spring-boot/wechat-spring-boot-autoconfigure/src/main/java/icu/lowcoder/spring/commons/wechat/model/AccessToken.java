package icu.lowcoder.spring.commons.wechat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken extends BaseResponse {
    private String accessToken;
    private Long expiresIn;
}
