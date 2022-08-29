package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneInfo {
    private String phoneNumber; // 用户绑定的手机号（国外手机号会有区号）
    private String purePhoneNumber; // 没有区号的手机号
    private String countryCode; // 没有区号的手机号
}
