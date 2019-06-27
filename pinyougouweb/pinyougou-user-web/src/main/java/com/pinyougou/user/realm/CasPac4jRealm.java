package com.pinyougou.user.realm;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

public class CasPac4jRealm extends Pac4jRealm {

    //设置角色和权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 获取登录用户名
        String username = ((Pac4jPrincipal) principals.getPrimaryPrincipal()).getName();
        System.out.println("username = " + username);
        return null;
    }

    //自定义身份认证域
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 调用Pac4jRealm实现的认证方法
        AuthenticationInfo authc = super.doGetAuthenticationInfo(token);
        // 获取登录用户名
        String username = ((Pac4jPrincipal) authc.getPrincipals().getPrimaryPrincipal()).getName();
        System.out.println("username = " + username);
        return authc;
    }
}
