package com.springboot.tmall.comparator;

import com.springboot.tmall.pojo.Product;

import java.util.Comparator;

/**
 * 按照销量x评价降序排列
 */
public class ProductAllComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount()*p2.getSaleCount()-p1.getReviewCount()*p1.getSaleCount();
    }
 
}