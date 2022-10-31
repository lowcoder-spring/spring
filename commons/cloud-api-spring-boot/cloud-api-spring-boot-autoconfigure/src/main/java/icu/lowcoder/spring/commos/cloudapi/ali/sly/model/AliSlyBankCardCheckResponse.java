package icu.lowcoder.spring.commos.cloudapi.ali.sly.model;

import lombok.Getter;
import lombok.Setter;

/*
"data": {
    "result": 0, //验证结果：0 一致；1 不一致
    "order_no": "208471536490989385",
    "msg": "一致",//验证结果
    "bank_info": {//银行卡归属地信息
        "bin_digits": 6,//银行卡bin码长度
        "city": "杭州",//银行卡开户市
        "type": "借记卡",//银行卡类型
        "abbreviation": "BOC",//银联支付网关简码
        "bank": "中国银行",//银行名称
        "province": "浙江",//银行卡开户省
        "card_digits": 19,//银行卡号长度
        "weburl": "http://www.bank-of-china.com/",//银行官方网站
        "card_name": "借记IC个人普卡",//银行卡名称
        "logo": "http://pic.dataox.com.cn/zhongguo.png",//银行logo
        "tel": "95566",//银行官方客服电话
        "isLuhn": true,//是否支持luhn校验
        "card_bin": "621785"//银行卡bin码
    },
    "desc": "认证信息匹配"//验证结果描述
}
 */
@Getter
@Setter
public class AliSlyBankCardCheckResponse extends CommonResponse {

    private Data data;

    @Getter
    @Setter
    public static class Data {
        private Integer result;
        private String orderNo;
        private String msg;
        private String desc;

        private BankInfo bankInfo;
    }

    @Getter
    @Setter
    public static class BankInfo {
        private Integer binDigits;
        private String city;
        private String type;
        private String abbreviation;
        private String bank;
        private String province;
        private Integer cardDigits;
        private String weburl;
        private String cardName;
        private String logo;
        private String tel;
        private String isLuhn;
        private String cardBin;
    }
}
