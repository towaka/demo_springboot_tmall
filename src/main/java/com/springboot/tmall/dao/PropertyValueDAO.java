package com.springboot.tmall.dao;

import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.pojo.Property;
import com.springboot.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    PropertyValue getByPropertyAndProduct(Property property, Product product);
}
