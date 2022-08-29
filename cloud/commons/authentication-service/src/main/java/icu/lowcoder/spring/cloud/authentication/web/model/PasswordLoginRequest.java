package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordLoginRequest {
    private String username;
    private String password;
}
