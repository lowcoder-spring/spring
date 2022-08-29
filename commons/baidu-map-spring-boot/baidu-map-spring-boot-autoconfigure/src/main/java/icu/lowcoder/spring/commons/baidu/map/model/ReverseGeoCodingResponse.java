package icu.lowcoder.spring.commons.baidu.map.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReverseGeoCodingResponse extends ApiResponse {
    private ReverseGeoCodingResult result;
}
