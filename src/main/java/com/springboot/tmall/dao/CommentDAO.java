package com.springboot.tmall.dao;

import com.springboot.tmall.pojo.Comment;
import com.springboot.tmall.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDAO extends JpaRepository<Comment,Integer> {
    List<Comment> findByProductOrderByIdDesc(Product product);
    int countByProduct(Product product);
}
