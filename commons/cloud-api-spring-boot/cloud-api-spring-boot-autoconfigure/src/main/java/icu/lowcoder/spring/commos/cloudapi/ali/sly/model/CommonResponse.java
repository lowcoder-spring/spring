package icu.lowcoder.spring.commos.cloudapi.ali.sly.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse  {
    private String msg;
    private Integer code;
    private Boolean success;
}
