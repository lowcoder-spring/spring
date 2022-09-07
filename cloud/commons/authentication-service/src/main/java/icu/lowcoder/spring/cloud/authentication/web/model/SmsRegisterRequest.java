package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRegisterRequest {
    private String phone;
    private String smsCode;
    private String name;
}
