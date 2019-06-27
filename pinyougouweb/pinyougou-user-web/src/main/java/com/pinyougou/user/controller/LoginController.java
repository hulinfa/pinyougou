package com.pinyougou.user.controller;

import io.buji.pac4j.subject.Pac4jPrincipal;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @GetMapping("/user/showName")
    public Map<String, String> showName() {
        /** 获取用户登录名 */
        Pac4jPrincipal principal = (Pac4jPrincipal) SecurityUtils.getSubject().getPrincipal();
        String loginName = principal.getName();
        Map<String, String> map = new HashMap<>();
        map.put("loginName", loginName);
        return map;
    }
}