package icu.lowcoder.spring.commons.robot.tencent;

import lombok.Data;

@Data
public class VerifyResponse {
    private Integer response;
    private Integer evilLevel;
    private String errMsg;
}
