package icu.lowcoder.spring.cloud.authentication.web.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AdminAccountsListItem {

    private String name;

    private String phone;

    private String email;

    private Date registerTime = new Date();

    private Boolean enabled = true;

    private List<String> authorities = new ArrayList<>();
}
