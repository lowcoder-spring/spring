package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String openId;
    private String nickName;
    private Integer gender;
    private String province;
    private String city;
    private String country;
    private String avatarUrl;
    private String unionId;

    private String appId;
    private String sessionKey;
}
