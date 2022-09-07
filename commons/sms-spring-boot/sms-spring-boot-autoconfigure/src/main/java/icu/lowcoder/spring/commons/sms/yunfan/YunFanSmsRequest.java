package icu.lowcoder.spring.commons.sms.yunfan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YunFanSmsRequest {
    private String sign;
    private String timestamp;
    private String phone;
    private String extend;
    private String appcode;
    private String appkey;
    private String msg;
}
