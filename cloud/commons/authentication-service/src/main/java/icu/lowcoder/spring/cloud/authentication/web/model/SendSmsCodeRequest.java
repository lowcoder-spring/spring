package icu.lowcoder.spring.cloud.authentication.web.model;

import icu.lowcoder.spring.cloud.authentication.dict.SmsCodeCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSmsCodeRequest {
    private String phone;
    private SmsCodeCategory category;
}
