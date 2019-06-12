package com.pinyougou.manager.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class LoginController {

    @RequestMapping("/login")
    public String login(String username, String password) {
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
        return "redirect:/login.html";
    }

    @RequestMapping("/findLoginName")
    @ResponseBody
    public Map<String, String> findLoginName() {
        String loginName = SecurityUtils.getSubject().getPrincipal().toString();
        Map<String, String> data = new HashMap<String, String>();
        data.put("loginName", loginName);
        return data;
    }
}
