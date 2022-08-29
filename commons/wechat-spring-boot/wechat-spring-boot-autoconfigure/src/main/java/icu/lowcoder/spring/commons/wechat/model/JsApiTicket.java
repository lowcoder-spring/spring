package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsApiTicket extends BaseResponse {
    private String ticket;
    private Long expiresIn;
}
