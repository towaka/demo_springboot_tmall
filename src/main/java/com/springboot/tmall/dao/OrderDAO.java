package com.springboot.tmall.dao;
  
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order,Integer>{
    /**
     * findByUser(user)AndStatusNot(status)OrderByIdDesc
     * @param user
     * @param status
     * @return
     */
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user,String status);
}