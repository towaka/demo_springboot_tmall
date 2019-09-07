package com.springboot.tmall.dao;
  
import java.util.List;

import com.springboot.tmall.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.OrderItem;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    List<OrderItem> findByProduct(Product product);
}