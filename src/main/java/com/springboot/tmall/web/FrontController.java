package com.springboot.tmall.web;
 
import com.springboot.tmall.comparator.*;
import com.springboot.tmall.pojo.*;
import com.springboot.tmall.service.*;
import com.springboot.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    OrderService orderService;
 
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

        //其实就是加多一个盐变量，加密次数，和想使用的加密算法
        //SecureRandomNumberGenerator适合用于shiro配置文件和其他切面编程配置的场景
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithm = "md5";
        //将算法名字，密码，盐，次数依次放进SimpleHash构造函数里
        String encodedPassword = new SimpleHash(algorithm,password,salt,times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/frontlogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        /*
        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            return Result.success();
        }
        */
        /*这段代码是为了把加入shiro前测试的账号的密码加盐*/
        User userWithoutSalt = userService.getByName(name);
        if(userWithoutSalt!=null){
            if(userWithoutSalt.getSalt()==null) {
                String salt = new SecureRandomNumberGenerator().nextBytes().toString();
                int times = 2;
                String algorithm = "md5";
                String originPassword = userWithoutSalt.getPassword();
                //将算法名字，密码，盐，次数依次放进SimpleHash构造函数里
                String encodedPassword = new SimpleHash(algorithm, originPassword, salt, times).toString();
                userWithoutSalt.setSalt(salt);
                userWithoutSalt.setPassword(encodedPassword);
                userService.add(userWithoutSalt);
            }
        }
        /*这段代码是为了把加入shiro前测试的账号的密码加盐*/

        //改成通过shiro方式进行校验
        Subject subject = SecurityUtils.getSubject();
        //将帐密封装成一个 UsernamePasswordToken 对象
        UsernamePasswordToken token = new UsernamePasswordToken(name,userParam.getPassword());
        try{
            //验证帐密,subject.login(token);成功了才往下执行，否则跳到catch块
            subject.login(token);
            User user = userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        }catch(AuthenticationException e){
            String info = "账号或者密码错误";
            System.out.println(userParam.getSalt());
            return Result.fail(info);
        }
    }

    @GetMapping("frontcheckLogin")
    public Object checkLogin( HttpSession session) {
        /*
        User user =(User)  session.getAttribute("user");
        if(null!=user)
            return Result.success();
        return Result.fail("未登录");
        */
        //改成Subject类校验
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated())
            return Result.success();
        else
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
     * 立即购买<br>
     *     <br>
     * 添加到购物车的过程中，是把产品id和购买数量拿到手，进行购物车页面跳转<br>
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
     * 添加到购物车
     * 添加到购物车的过程中，是把产品id和购买数量拿到手，进行购物车页面跳转<br>
     * 这里直接就把这部分的业务逻辑重构到
     * {@link FrontController#buyAndAddInCart(int, int, HttpSession)}方法中<br>
     * @param pid
     * @param num
     * @param session
     * @return
     */
    @GetMapping("frontaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyAndAddInCart(pid,num,session);
        return Result.success();
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
     * <br>
     * <br>
     * 看代码可以看出，这里需要判断当前订单里是否已经存在这个产品对应的订单项（OrderItem）<br>
     * 1. 如果存在，就增加订单项里对应产品的数量，把修改保存到数据库，并且返回订单项id方便作跳转<br>
     * 2. 如果不存在，就持久化一个新的订单项（OrderItem）对象，完善订单项信息，例如：<br>
     *  ┗ 2.1 当前订单项属于哪个用户创建<br>
     *  ┗ 2.2 当前订单项包含什么产品<br>
     *  ┗ 2.3 当前订单项里的产品总共多少个<br>
     *  ┗ 2.4 返回订单项id方便作跳转<br>
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

    /**
     * 1.通过数组获得订单项id组，无论客户是购买一个或者多个商品都拿这个数组记录订单项id<br>
     * 2.准备一个订单项集合<br>
     * 3.拿到id后<br>
     *  ┗ 3.1 将字符串转化为int类型数据<br>
     *  ┗ 3.2 通过id取得订单项数据并放在持久化对象里<br>
     *  ┗ 3.3 计算当前订单项里 产品当期实际价格*购买数量 得出此订单项总价<br>
     *  ┗ 3.4 得到前面的总价后将添加进去订单项集合<br>
     * 4.处理订单项预览图<br>
     * 5.把订单项集合数据放在session里面<br>
     * 6.把订单集合和total 放在map里<br>
     * 7.用Result.success的形式返回<br>
     * @param oiid
     * @param session
     * @return
     */
    @GetMapping("frontbuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProductImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * 1.这里第一部先从session拿到用户，这代表用户必须先要有登录行为<br>
     * 2.然后按照用户寻找目前“还没有结算”的订单项<br>
     * 3.然后给订单项填充预览图<br>
     * 4.返回这个订单项集合<br>
     * @param session
     * @return
     */
    @GetMapping("frontcart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }

    /**
     * 1.判断用户是否登录<br>
     * 2. 获取pid和number<br>
     * 3. 遍历出用户当前所有的未结算的订单OrderItem<br>
     * 4. 根据pid找到匹配的OrderItem，并修改数量后更新到数据库<br>
     * 5. 返回 Result.success()<br>
     * @param session
     * @param pid
     * @param num
     * @return
     */
    @GetMapping("frontchageOrderItem")
    public Object changeOrderItem(HttpSession session,int pid,int num){
        User user = (User) session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");

        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==pid){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }

    /**
     * 1. 判断用户是否登录<br>
     * 2. 获取oiid<br>
     * 3. 删除oiid对应的OrderItem数据<br>
     * 4. 返回字符串 Result.success<br>
     * @param session
     * @param oiid
     * @return
     */
    @GetMapping("frontdeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

    /**
     * 创建订单<br>
     * <br>
     * 1.管理先检查是否有登录，一般来说进入这方法时用户都应该登录了的<br>
     * 2.开始处理订单内部信息<br>
     *  ┗ 2.1 订单号<br>
     *  ┗ 2.2 订单创建日期<br>
     *  ┗ 2.3 记录创建订单的用户<br>
     *  ┗ 2.4 记录订单目前状态<br>
     * 3.提交订单<br>
     * 4.以Map的形式记录订单id和订单总价<br>
     * 5.用Result返回<br>
     *
     * 这里ois接受数据经过的session是从{@link FrontController#buy(String[], HttpSession)}
     * 方法过来的，订单项集合被放到了session中
     * @param order
     * @param session
     * @return
     */
    @PostMapping("frontcreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);

        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);


        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");
        float total =orderService.add(order,ois);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }

    /**
     * 确定已付款的订单<br>
     * @param oid
     * @return
     */
    @GetMapping("frontpayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    /**
     * 确定已购买产品的订单<br>
     * @param session
     * @return
     */
    @GetMapping("frontbought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }

    /**
     * 确认收货<br>
     * @param oid
     * @return
     */
    @GetMapping("frontconfirmPay")
    public Object confirmPay(int oid){
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }

    /**
     * 确认收货成功
     * @param oid
     * @return
     */
    @GetMapping("frontorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);//记住要改变订单状态
        o.setConfirmDate(new Date());
        orderService.update(o);//及时更新到数据库
        return Result.success();
    }

    /**
     * 删除已交易的订单
     * 注意，这里虽然是删除功能，可本质上是修改（update），所以注解是@PutMapping
     * @param oid
     * @return
     */
    @PutMapping("frontdeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    /**
     * 收货成功后的评论
     * @param oid
     * @return
     */
    @GetMapping("frontcomment")
    public Object comment(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        //因为这里会把Order对象持久化，所以要把订单项里面的订单属性去除，以免发生无限递归
        orderService.removeOrderFromOrderItem(o);
        //这里只对第一个订单项对应的产品进行评论，因为对多个产品进行评论的部分实在不会做- -
        Product p = o.getOrderItems().get(0).getProduct();
        List<Comment> comments = commentService.list(p);
        productService.setSaleAndReviewNumber(p);

        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("comments", comments);

        return Result.success(map);
    }

    /**
     * 1.先获得订单
     * 2.然后修改订单状态
     * 3.订单状态更新到数据库
     * 4.获取产品
     * 5.对评论信息进行转义
     * 6.检查是否登录
     * 7.创建评论类对象
     *  ┗ 7.1 填充评论信息
     *  ┗ 7.2 设置评论所对应的产品
     *  ┗ 7.3 设置评论日期
     *  ┗ 7.4 设置评论的用户
     * 8.将评论更新到数据库
     * @param session
     * @param oid
     * @param pid
     * @param content
     * @return
     */
    @PostMapping("frontdocomment")
    public Object doreview( HttpSession session,int oid,int pid,String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Comment review = new Comment();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        commentService.add(review);
        return Result.success();
    }
}