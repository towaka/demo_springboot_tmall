package com.springboot.tmall.service;
 
import java.util.List;

import com.springboot.tmall.dao.CommentDAO;
import com.springboot.tmall.pojo.Comment;
import com.springboot.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 
@Service
public class CommentService {
 
    @Autowired
    CommentDAO commentDAO;
    @Autowired ProductService productService;
 
    public void add(Comment comment) {
        commentDAO.save(comment);
    }
 
    public List<Comment> list(Product product){
        List<Comment> result =  commentDAO.findByProductOrderByIdDesc(product);
        return result;
    }
 
    public int getCount(Product product) {
        return commentDAO.countByProduct(product);
    }
     
}