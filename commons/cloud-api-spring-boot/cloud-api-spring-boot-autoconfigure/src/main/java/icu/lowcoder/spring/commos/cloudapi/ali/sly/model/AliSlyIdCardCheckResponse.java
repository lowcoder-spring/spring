package icu.lowcoder.spring.commos.cloudapi.ali.sly.model;

import lombok.Getter;
import lombok.Setter;

/*
"data": {
    "result": 1, // 0:一致 1:不一致 2:无记录
    "order_no": "626072002058391552",
    "desc": "不一致",
    "sex": "男",
    "birthday": "19940320",
    "address": "江西省南昌市东湖区"
}
 */
@Getter
@Setter
public class AliSlyIdCardCheckResponse extends CommonResponse {

    private Data data;

    @Getter
    @Setter
    public static class Data {
        private Integer result;
        private String orderNo;
        private String desc;
        private String sex;
        private String birthday;
        private String address;
    }
}
