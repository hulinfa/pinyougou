package com.pinyougou.cart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @RequestMapping("/user/showName")
    public Map<String, String> showName(HttpServletRequest request) {
        String name = request.getRemoteUser();
        HashMap<String, String> map = new HashMap<>();
        map.put("loginName", name);
        return map;
    }

}
