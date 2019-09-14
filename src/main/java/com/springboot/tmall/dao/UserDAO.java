package com.springboot.tmall.dao;
  
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.springboot.tmall.pojo.User;
 
public interface UserDAO extends JpaRepository<User,Integer>{
    User findByName(String name);
    User getByNameAndPassword(String name,String password);
    User findById(int id);
}