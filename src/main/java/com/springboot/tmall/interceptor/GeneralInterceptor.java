package com.springboot.tmall.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.pojo.OrderItem;
import com.springboot.tmall.pojo.User;
import com.springboot.tmall.service.CategoryService;
import com.springboot.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 页面中其他行为也可以通过拦截器来进行拦截
 */
public class GeneralInterceptor implements HandlerInterceptor {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    /**
     * 做了三件事儿
     * 1.更新右上角购物车里的产品数
     * 2.提供分类信息显示在搜索栏下面
     * 3.点击左上角logo进行跳转至首页
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //先获取session
        HttpSession session = httpServletRequest.getSession();
        //先从session拿到用户信息
        User user =(User) session.getAttribute("user");
        int  cartTotalItemNumber = 0;
        //统计指定用户的订单项的产品购买总数
        if(null!=user) {
            List<OrderItem> ois = orderItemService.listByUser(user);
            for (OrderItem oi : ois) {
                cartTotalItemNumber+=oi.getNumber();
            }
        }
        //获取分类信息，方便在搜索栏下面的种类栏里显示
        List<Category> cs =categoryService.list();
        //获取本项目的首页跳转
        String contextPath=httpServletRequest.getServletContext().getContextPath();

        /*
        * 下面三个是将会被传递到前端的数据，但可以留意到有
        * httpServletRequest.getServletContext().setAttribute 和 session.setAttribute
        * 这里面涉及thymeleaf沿袭于JSP的作用域概念
        * httpServletRequest.getServletContext().setAttribute()对应搜索栏代码中的${application.categories_below_search}"，
        * 这里是面向全局的设置，所有用户都能获得同样的跳转链接 和 获得搜索栏下的分类推荐
        *
        * session.setAttribute所传输的购物车数量数据，逻辑上是只需要当前会话的用户自己看到就行了的。
        * 所传递的数据是面向一个用户的，session作为会话，从一个用户打开网站的那一刻起，
        * 无论访问了多少网页，链接都属于同一个会话，直到浏览器关闭。所以页面间传递数据，也是可以通过session传递的。
        * 但是，不同用户对应的session是不一样的，所以session无法在不同的用户之间共享数据。
        * */
        httpServletRequest.getServletContext().setAttribute("categories_below_search", cs);
        session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
        httpServletRequest.getServletContext().setAttribute("contextPath", contextPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }


}