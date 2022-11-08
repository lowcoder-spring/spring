package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateAccountAuthoritiesRequest {

    private List<String> authorities = new ArrayList<>();
}
