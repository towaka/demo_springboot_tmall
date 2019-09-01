package com.how2java.tmall.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Page4Navigator;

/**
 * 一般来说只需要查询，
 * 因为用户是需要使用者自己去注册的（增加）
 * 修改用户信息也应该是用户自己完成（修改）
 */
@RestController
public class UserController {
    @Autowired UserService userService;
 
    @GetMapping("/users")
    public Page4Navigator<User> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                     @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<User> page = userService.list(start,size,5);
        return page;
    }
         
}