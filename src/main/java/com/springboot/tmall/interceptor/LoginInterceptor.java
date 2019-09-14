package com.springboot.tmall.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.springboot.tmall.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 购买产品和进入购物车页面是需要用户登录的，但很多时候用户自己不记得登录<br/>
 * 那我们就准备一个拦截器<br/>
 * 当访问那些需要登录才能做的页面的时候，进行是否登录的判断，<br/>
 * 如果不通过，那么就跳转到登录页面去，提示用户登录。<br/>
 *
 * 先确定需要登陆与否的页面<br/>
 * 不需要登陆：注册，登录，产品，首页，查询结果，分类<br/>
 * 需要登陆：购物车页面，订单页面，以及购买产品和加入购物车两种行为<br/>
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws Exception {
        //先拿到session，方便之后检查登录状态
        HttpSession session = httpServletRequest.getSession();
        //通过session获取上下文，拿到本项目的路径位置
        //（这里是@see springboot_tmall,application.properties文件里有标注）
        String contextPath=session.getServletContext().getContextPath();
        String[] requireLoginPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",
                "frontbuyone",
                "frontbuy",
                "frontaddCart",
                "frontcart",
                "frontchangeOrderItem",
                "frontdeleteOrderItem",
                "frontcreateOrder",
                "frontpayed",
                "frontbought",
                "frontconfirmPay",
                "frontorderConfirmed",
                "frontdeleteOrder",
                "frontreview",
                "frontdoreview"
        };

        //结合本项目环境，uri的值= springboot_tmall/requireLoginPages数组里的值
        String uri = httpServletRequest.getRequestURI();
        //然后这里把 springboot_tmall/ 去掉
        uri = StringUtils.remove(uri, contextPath+"/");
        //剩下的字符串
        String page = uri;
        //如果和数组里的某一个值相同

        /*if(begingWith(page, requireLoginPages)){
            User user = (User) session.getAttribute("user");
            if(user==null) {
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }*/

        //改成Subject类校验
        if(begingWith(page,requireLoginPages)){
            Subject subject = SecurityUtils.getSubject();
            if(!subject.isAuthenticated()){
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        return true;  
    }
 
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
 
    }
 
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    private boolean begingWith(String page, String[] requireLoginPages) {
        boolean result = false;
        for (String requireLoginPage : requireLoginPages) {
            if(StringUtils.startsWith(page, requireLoginPage)) {
                result = true;
                break;
            }
        }
        return result;
    }
}