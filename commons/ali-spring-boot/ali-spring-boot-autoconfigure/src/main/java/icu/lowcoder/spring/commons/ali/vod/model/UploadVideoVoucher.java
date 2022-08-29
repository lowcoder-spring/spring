package icu.lowcoder.spring.commons.ali.vod.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UploadVideoVoucher {
    private String requestId;
    private String videoId;
    private String uploadAddress;
    private String uploadAuth;
}
