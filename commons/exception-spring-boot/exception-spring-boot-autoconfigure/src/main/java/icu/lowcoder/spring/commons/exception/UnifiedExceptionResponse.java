package icu.lowcoder.spring.commons.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UnifiedExceptionResponse {
    @Builder.Default
    private Date timestamp = new Date();
    @Builder.Default
    private Integer status = 500;
    private String exception;
    private String message;
    private String uri;
    private String detail;
}
