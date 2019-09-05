package com.springboot.tmall.web;
 
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.pojo.User;
import com.springboot.tmall.service.CategoryService;
import com.springboot.tmall.service.ProductService;
import com.springboot.tmall.service.UserService;
import com.springboot.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 在做后台设计时，分类、用户、产品、属性等都有自己对应的控制层
 * 但是前台则是各种服务的糅合
 * 死板地对应某个控制层来设计也不合适，那么就开一个控制类来响应前台页面的路径
 */
@RestController
public class FrontController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
 
    @GetMapping("/fronthome")
    public Object home() {
        List<Category> cs= categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);//如果不加这个就会产生无限递归问题（Infinite Recursion）
        return cs;
    }

    @PostMapping("/frontregister")
    public Object register(@RequestBody User user) {
        String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist){
            String message ="用户名已经被使用,不能使用";
            return Result.fail(message);
        }

        user.setPassword(password);
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/frontlogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            return Result.success();
        }
    }
}