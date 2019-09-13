package com.springboot.tmall.service;
 
import com.springboot.tmall.dao.OrderDAO;
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.OrderItem;
import com.springboot.tmall.pojo.User;
import com.springboot.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
 
@Service
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";  
     
    @Autowired OrderDAO orderDAO;
    @Autowired OrderItemService orderItemService;

    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    /**
     * 本方法的用途是把订单里的订单项的订单属性设置为空<br/>
     * <br/>
     * 比如有个 order, 拿到它的 orderItems， 然后再把这些orderItems的order属性，设置为空。<br/>
     * 为什么要做这个事情呢？因为SpringMVC(springboot 里内置的mvc框架是 这个东西)的 RESTFUL注解，<br/>
     * 在把一个Order转换为json的同时，会把其对应的 orderItems 转换为 json数组，<br/>
     * 而 orderItem对象上有 order属性，这个order属性又会被转换为json对象，<br/>
     * 然后这个order下又有 orderItems.....就这样就会产生无穷递归，系统就会报错了。避免无限递归<br/>
     * 注意，无穷递归的问题，只会在 对象持久化到 redis 的时候产生<br/>
     * <br/>
     * 注：这里方法域改成了public，因为后来在
     * {@link com.springboot.tmall.web.FrontController#confirmPay(int)}方法里需要使用这个方法
     * @param order
     */
    /*private*/ public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems= order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }
 
    public Order get(int oid) {
        return orderDAO.findOne(oid);
    }
 
    public void update(Order bean) {
        orderDAO.save(bean);
    }

    public void add(Order bean){
        orderDAO.save(bean);
    }

    /**
     * 以防增加订单后产生表和表数据不对的脏数据<br/>
     * 做事务回滚，直接抛出异常<br/>
     * 1.给订单项设置对应的订单属性（订单号）
     * 2.将第一步的设置更新到数据库
     * 3.统计所有订单项加起来的总价
     * @param order
     * @param ois
     * @return
     */
    @Transactional(propagation =Propagation.REQUIRED,rollbackForClassName = "Exception")
    public float add(Order order,List<OrderItem> ois){
        float total = 0;
        add(order);

        if(false){
            throw new RuntimeException();
        }

        for (OrderItem oi: ois) {
            oi.setOrder(order);//无穷递归的问题，只会在 对象持久化到 redis 的时候产生。这里已经不是做持久化了，所以就没有关系了.
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;
    }

    public List<Order> listByUserAndNotDelete(User user){
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user,OrderService.delete);
    }

    public List<Order> listByUserWithoutDelete(User user){
        List<Order> orders = listByUserAndNotDelete(user);
        orderItemService.fill(orders);
        return orders;
    }
}