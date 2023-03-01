package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AddWeChatWebAppConnectionRequest {

    @NotBlank
    private String appId;

    @NotBlank
    private String code;

}
