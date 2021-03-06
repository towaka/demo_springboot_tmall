package com.springboot.tmall.test;
   
import com.springboot.tmall.pojo.User;
import com.springboot.tmall.service.UserService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;

import java.sql.*;

/**
 * 使用JUNIT测试方法
 * 使用过程中，方法不能被声明为static，也不能有方法参数
 */
public class TestTmall {
    UserService userService;

    @Test
    public void test1(){
        //准备分类测试数据：
   
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
   
        try (
                Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tmall_springboot?useUnicode=true&characterEncoding=utf8",
                        "root", "admin");
                Statement s = c.createStatement();
        )
        {
            for (int i = 1; i <=10 ; i++) {
                String sqlFormat = "insert into category values (null, '测试分类%d')";
                String sql = String.format(sqlFormat, i);
                s.execute(sql);
            }
              
            System.out.println("已经成功创建10条分类测试数据");
   
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
   
    }
}