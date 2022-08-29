package icu.lowcoder.spring.cloud.authentication.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/account")
@RestController
public class AccountController {

    @GetMapping("/profile")
    public Object profile(Authentication authentication) {
        // TODO profile
        return authentication;
    }
}
