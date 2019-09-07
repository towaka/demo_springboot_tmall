package com.springboot.tmall.comparator;

import com.springboot.tmall.pojo.Product;

import java.util.Comparator;

/**
 * 按照产品创建日期降序排列
 */
public class ProductDateComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getCreateDate().compareTo(p1.getCreateDate());
    }
}