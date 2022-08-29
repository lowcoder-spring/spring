package icu.lowcoder.spring.commons.wechat.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TemplateMessageDataValue {
    private String color = "#2db7f5";
    private String value;

    public TemplateMessageDataValue(String value) {
        this.value = value;
    }
}
