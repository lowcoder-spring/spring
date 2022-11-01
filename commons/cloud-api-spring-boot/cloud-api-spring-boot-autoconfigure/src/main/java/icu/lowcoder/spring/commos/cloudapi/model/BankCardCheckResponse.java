package icu.lowcoder.spring.commos.cloudapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankCardCheckResponse {
    private Boolean passed; // 是否验证通过
    private String desc; // 描述

    // 通过后返回的信息
    private BankCardType type; // 卡类型
    private String abbreviation; // 简码
    private String logo; // logo
    private String bank; // 银行名称
}
