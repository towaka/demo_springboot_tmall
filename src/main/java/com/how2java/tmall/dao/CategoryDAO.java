package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 在ssh和ssm下，DAO一般是类，里面有各种crud方法和连接数据库的功能
 * 在此项目下，XxxDAO是接口，继承自JpaRepository，JpaRepository提供CRUD、分页 各种常见功能
 * 并且JpaRepository支持接口规范方法名查询。意思是如果在接口中定义的方法名符合它的命名规则，就可以不用写实现
 */
public interface CategoryDAO extends JpaRepository<Category,Integer>{

}
