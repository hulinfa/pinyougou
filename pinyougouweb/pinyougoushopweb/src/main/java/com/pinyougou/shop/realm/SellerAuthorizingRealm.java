package com.pinyougou.shop.realm;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class SellerAuthorizingRealm extends AuthorizingRealm {

    @Reference(timeout = 10000)
    private SellerService sellerService;

    /*授权方法*/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 身份认证方法
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        String username = authenticationToken.getPrincipal().toString();
        System.out.println("username=" + username);
        System.out.println("sellerService" + sellerService);
        try {
            Seller seller = sellerService.findOne(username);
            if (seller != null && "1".equals(seller.getStatus())) {
                return new SimpleAuthenticationInfo(username, seller.getPassword(), this.getName());
            }
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
        return null;
    }


}
