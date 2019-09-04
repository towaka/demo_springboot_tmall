package com.springboot.tmall.dao;

import java.util.List;

import com.springboot.tmall.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.tmall.pojo.ProductImage;

public interface ProductImageDAO extends JpaRepository<ProductImage,Integer>{
    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}