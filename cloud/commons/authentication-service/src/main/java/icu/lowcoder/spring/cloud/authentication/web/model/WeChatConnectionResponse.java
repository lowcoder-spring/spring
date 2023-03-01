package icu.lowcoder.spring.cloud.authentication.web.model;

import icu.lowcoder.spring.cloud.authentication.dict.WeChatAppType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatConnectionResponse {

    private String appId;
    private String openId;
    private String nickname;
    private Integer gender;
    private String city;
    private String country;
    private String avatar;
    private String unionId;
    private WeChatAppType appType;

}
