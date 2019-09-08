package com.springboot.tmall.dao;

import com.springboot.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 在ssh和ssm下，DAO一般是类，里面有各种crud方法和连接数据库的功能<br/>
 * 在此项目下，XxxDAO是接口，继承自JpaRepository，JpaRepository提供CRUD、分页 各种常见功能<br/>
 */
public interface CategoryDAO extends JpaRepository<Category,Integer>{

}
