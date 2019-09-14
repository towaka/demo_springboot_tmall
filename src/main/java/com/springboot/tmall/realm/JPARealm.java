package com.springboot.tmall.realm;

import com.springboot.tmall.pojo.User;
import com.springboot.tmall.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class JPARealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    /**
     * 授权方法
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //SimpleAuthorizationInfo用于对用户进行授权（角色和权限）
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    /**
     * 验证方法
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //拿到形参，用其通过数据库获取账号密码
        String userName = token.getPrincipal().toString();
        User user = userService.getByName(userName);
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        //认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :JPARealm
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo
                (userName,
                 passwordInDB,
                 ByteSource.Util.bytes(salt),
                 getName());
        return simpleAuthenticationInfo;
    }
}
