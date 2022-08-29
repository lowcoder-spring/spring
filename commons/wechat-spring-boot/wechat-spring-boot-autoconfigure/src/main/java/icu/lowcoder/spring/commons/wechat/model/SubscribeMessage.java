package icu.lowcoder.spring.commons.wechat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SubscribeMessage {
    private String touser; // 接收者（用户）的 openid
    private String template_id; // 所需下发的订阅模板id
    @Setter
    private String page; // 模板跳转链接（海外帐号没有跳转能力）
    @Setter
    private Map<String, SubscribeMessageDataValue> data = new HashMap<>(); // 模板内容
    private String miniprogram_state;//跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版
    @Setter
    private String lang;

    public void setToUser(String openId) {
        this.touser = openId;
    }

    public void setTemplate(String templateId) {
        this.template_id = templateId;
    }

    public void setMiniProgramState(String miniProgramState) {
        this.miniprogram_state = miniprogram_state;
    }

}
