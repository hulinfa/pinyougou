package com.pinyougou.shop.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @PostMapping("/login")
    public String Login(String username, String password) {
        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            subject.login(token);
            if (subject.isAuthenticated()) {
                return "redirect:/admin/index.html";
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        return "redirect:/shoplogin.html";
    }

    /**
     * 获取登录用户名
     */
    @GetMapping("/findLoginName")
    @ResponseBody
    public Map<String, String> findLoginName() {
        // 获取登录用户名
        String loginName = SecurityUtils.getSubject()
                .getPrincipal().toString();
        Map<String, String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }

}
