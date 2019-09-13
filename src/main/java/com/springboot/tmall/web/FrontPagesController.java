package com.springboot.tmall.web;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * 前台页面跳转
 */
@Controller
public class FrontPagesController {
    @GetMapping(value="/")
    public String index(){
        return "redirect:home";
    }

    @GetMapping(value="/home")
    public String home(){
        return "front/home";
    }

    @GetMapping(value="/register")
    public String register(){
        return "front/register";
    }

    @GetMapping(value="/registerSuccess")
    public String registerSuccess(){
        return "front/registerSuccess";
    }

    @GetMapping(value="/login")
    public String login(){
        return "front/login";
    }
    /**
     * 注意logout方法在不需要在FrontController类声明对应方法，
     * 登出操作不需要返回 json数据，只需要对session进行操作即可
     * @see FrontController
     * @param session
     * @return
     */
    @GetMapping("/frontlogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:home";
    }

    @GetMapping(value="/product")
    public String product(){
        return "front/product";
    }

    @GetMapping(value="/search")
    public String searchResult(){
        return "front/generalSearch";
    }

    @GetMapping(value="/buy")
    public String buy(){
        return "front/buy";
    }

    @GetMapping(value="/cart")
    public String cart() {
        return "front/cart";
    }

    @GetMapping(value="/alipay")
    public String alipay(){
        return "front/alipay";
    }

    @GetMapping(value="/confirmPay")
    public String confirmPay(){
        return "front/confirmPay";
    }

    @GetMapping(value="/payed")
    public String payed(){
        return "front/payed";
    }

    @GetMapping(value="/bought")
    public String bought(){
        return "front/bought";
    }

    @GetMapping(value="/orderConfirmed")
    public String orderConfirmed(){
        return "front/orderConfirmed";
    }

    @GetMapping(value="/comment")
    public String review(){
        return "front/comment";
    }
}