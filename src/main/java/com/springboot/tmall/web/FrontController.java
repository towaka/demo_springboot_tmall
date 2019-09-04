package com.springboot.tmall.web;
 
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.service.CategoryService;
import com.springboot.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
 
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
 
    @GetMapping("/fronthome")
    public Object home() {
        List<Category> cs= categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }   
}