package icu.lowcoder.spring.commons.sms.yunfan;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class YunFanSmsResponse {
    private String code;
    private String desc;
    private String uid;
    private List<Result> result = new ArrayList<>();

    @Getter
    @Setter
    public static class Result {
        private String status;
        private String phone;
        private String desc;
    }
}
