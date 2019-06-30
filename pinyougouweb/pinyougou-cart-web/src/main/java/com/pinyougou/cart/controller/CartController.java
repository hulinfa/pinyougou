package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Cart;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 10000)
    private CartService cartService;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private HttpServletResponse response;

    @GetMapping("/addCart")
    @CrossOrigin(origins = "http://item.pinyougou.com", allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num) {
//        /** 设置允许访问的域名 */
//        response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
//        /** 设置允许操作Cookie */
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            //获取登录用户名
            String username = request.getRemoteUser();
            List<Cart> carts = findCart();
            carts = cartService.addItemToCart(carts, itemId, num);

            //判断用户是否登录
            if (StringUtils.isNoneBlank(username)) {//已经登录
                //往redis存储购物车
                cartService.saveCartRedis(username, carts);
            } else {//未登录
                //将购物车重新存入cookies中。
                CookieUtils.setCookie(request, response, CookieUtils.CookieName.PINYOUGOU_CART, JSON.toJSONString(carts), 3600 * 24, true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取购物车集合。
     *
     * @return
     */
    @GetMapping("/findCart")
    public List<Cart> findCart() {
        //获取登录用户名
        String username = request.getRemoteUser();
        List<Cart> carts = null;
        //判断用户是否登录
        if (StringUtils.isNoneBlank(username)) {
            carts = cartService.findCartRedis(username);
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isNoneBlank(cartStr)) {
                List<Cart> cookieCarts = JSON.parseArray(cartStr, Cart.class);
                if (cookieCarts != null && cookieCarts.size() > 0) {
                    //合并购物车
                    carts = cartService.mergeCart(cookieCarts, carts);
                    //将合并后购物车存入redis
                    cartService.saveCartRedis(username, carts);
                    //删除cookie购物车
                    CookieUtils.deleteCookie(request, response, CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }

        } else {
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isBlank(cartStr)) {
                cartStr = "[]";
            }
            carts = JSON.parseArray(cartStr, Cart.class);
        }
        return carts;
    }


}
