package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountProfile {
    private UUID userId;
    private String name;
    private String email;
    private List<String> authorities = new ArrayList<>();
}
