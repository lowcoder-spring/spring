package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebUserAccessToken extends BaseResponse {
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private String openid;
    private String scope;
    private String unionid;
    private Integer isSnapshotuser;
}
