package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebTemplateMessageSendResponse extends BaseResponse {
    private String msgid;
}
