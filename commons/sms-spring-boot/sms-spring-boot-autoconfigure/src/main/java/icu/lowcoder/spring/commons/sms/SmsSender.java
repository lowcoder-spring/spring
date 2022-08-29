package icu.lowcoder.spring.commons.sms;

import java.util.List;

public abstract class SmsSender {

    /**
     * 单条发送短信
     * @param target 目标手机号码
     * @param content 发送内容
     * @param type 短信类型;分为验证码和其他
     * @throws SmsSendException 异常
     */
    abstract public void send(String target, String content, SmsType type) throws SmsSendException;
    public void send(String target, String content) throws SmsSendException {
        this.send(target, content, SmsType.OTHER);
    }

    /**
     * 多目标发送相同短信
     * @param targets 目标手机号（可以配置批量发送限制）
     * @param content 发送内容
     * @throws SmsSendException 异常
     */
    abstract public void send(List<String> targets, String content) throws SmsSendException;
}
