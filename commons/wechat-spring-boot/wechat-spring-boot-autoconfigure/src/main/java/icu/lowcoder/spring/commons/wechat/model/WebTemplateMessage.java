package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WebTemplateMessage {
    private String touser; // 接收者（用户）的 openid
    private String template_id; // 所需下发的订阅模板id
    @Setter
    private String url; // 模板跳转链接（海外帐号没有跳转能力）
    private MiniProgramLink miniprogram; // 跳小程序所需数据，不需跳小程序可不用传该数据
    @Setter
    private Map<String, TemplateMessageDataValue> data = new HashMap<>(); // 模板内容

    public void setToUser(String openId) {
        this.touser = openId;
    }

    public void setTemplate(String templateId) {
        this.template_id = templateId;
    }

    public void setMiniProgramLink(MiniProgramLink link) {
        this.miniprogram = link;
    }

}
