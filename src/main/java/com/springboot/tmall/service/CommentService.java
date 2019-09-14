package com.springboot.tmall.service;
 
import java.util.List;

import com.springboot.tmall.dao.CommentDAO;
import com.springboot.tmall.pojo.Comment;
import com.springboot.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

 
@Service
@CacheConfig(cacheNames="comments")
public class CommentService {
 
    @Autowired
    CommentDAO commentDAO;
    @Autowired ProductService productService;

    @CacheEvict(allEntries = true)
    public void add(Comment comment) {
        commentDAO.save(comment);
    }

    @Cacheable(key="'comments-pid-'+#p0.id")
    public List<Comment> list(Product product){
        List<Comment> result =  commentDAO.findByProductOrderByIdDesc(product);
        return result;
    }

    @Cacheable(key="'comments-count-pid-'+ #p0.id")
    public int getCount(Product product) {
        return commentDAO.countByProduct(product);
    }
     
}