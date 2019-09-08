package com.springboot.tmall.web;
 
import com.springboot.tmall.comparator.*;
import com.springboot.tmall.pojo.*;
import com.springboot.tmall.service.*;
import com.springboot.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CommentService commentService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderItemService orderItemService;
 
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

    @GetMapping("frontcheckLogin")
    public Object checkLogin( HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null!=user)
            return Result.success();
        return Result.fail("未登录");
    }

    /**
     * 1.处理图片<br>
     * 2.处理属性值<br>
     * 3.处理评论<br>
     * @param pid
     * @return
     */
    @GetMapping("/frontproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Comment> comments = commentService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        //这里是要返回多个集合，用map方便浏览器解读数据
        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("comments", comments);

        return Result.success(map);
    }

    @GetMapping("frontcategory/{cid}")
    public Object category(@PathVariable int cid,String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);//搞死个人了，这发生无限递归问题

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return category;
    }

    @PostMapping("frontsearch")
    public Object search( String keyword){
        if(null==keyword)
            keyword = "";
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProductImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    /**
     * 立即购买和添加到购物车的功能都是一样的，
     * 添加到购物车的过程中，都是把产品id和购买数量拿到手，进行购物车页面跳转<br>
     * 这里直接就把这部分的业务逻辑重构到
     * {@link FrontController#buyAndAddInCart(int, int, HttpSession)}方法中<br>
     * @param pid
     * @param num
     * @param session
     * @return
     */
    @GetMapping("frontbuyInstantly")
    public Object buyInstantly(int pid, int num, HttpSession session) {
        return buyAndAddInCart(pid,num,session);
    }

    /**
     * 业务行为本身就是增加订单项<br>
     * 1. 获取参数pid<br>
     * 2. 获取参数num<br>
     * 3. 根据pid获取产品对象p<br>
     * 4. 从session中获取用户对象user<br>
     * 5. 返回当前订单项id<br>
     * 6. 在页面上，拿到这个订单项id，就跳转到 location.href="buy?oiid="+oiid;<br>
     * <br>
     * 看代码可以看出，这里需要判断当前订单里是否已经存在这个产品对应的订单项（OrderItem）<br>
     * 1. 如果存在，就增加订单项里对应产品的数量，把修改保存到数据库，并且返回订单项id方便作跳转<br>
     * 2. 如果不存在，就持久化一个新的订单项（OrderItem）对象，完善订单项信息，例如：<br>
     *  ┗ 2.1 当前订单项属于哪个用户创建<br>
     *  ┗ 2.2 当前订单项包含什么产品<br>
     *  ┗ 2.3 当前订单项里的产品总共多少个<br>
     *  ┗ 2.4 返回订单项id方便作跳转<br>
     *
     * @param pid
     * @param num
     * @param session
     * @return
     */
    private int buyAndAddInCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;

        User user =(User)  session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }
}