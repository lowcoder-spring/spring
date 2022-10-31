package icu.lowcoder.spring.commos.cloudapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdCardCheckResponse {
    private Boolean passed; // 是否验证通过
    private String desc; // 描述
}
