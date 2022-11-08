package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class AddAccountRequest {
    private String email;
    private Boolean enabled = true;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;

    private Set<String> authorities = new HashSet<>();
}
