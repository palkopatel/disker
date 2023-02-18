package com.disker.controller;

import com.disker.config.DiskRestClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Log4j2
@RestController
public class UserController {

    @Value("${spring.security.oauth2.client.provider.yandex.user-name-attribute}")
    private String userName;
    @Autowired
    @Lazy
    private DiskRestClient restClient;

    @GetMapping("user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        log.info("Get User");
        return Collections.singletonMap(userName, principal.getAttribute(userName));
    }

    @GetMapping("token")
    public String token() {
        log.info("Get Token");
        return restClient.getAccessToken();
    }
}
