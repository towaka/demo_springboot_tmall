package com.springboot.tmall.dao;
  
import java.util.List;

import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.OrderItem;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);//按照订单寻找订单项
    List<OrderItem> findByProduct(Product product);//按照产品寻找对应产品的订单项
    List<OrderItem> findByUserAndOrderIsNull(User user);//按照用户寻找还没结算的订单项
}