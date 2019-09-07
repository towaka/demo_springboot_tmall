package com.springboot.tmall.comparator;

import com.springboot.tmall.pojo.Product;

import java.util.Comparator;

/**
 * 产品按评论数目降序排列
 */
public class ProductReviewComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount()-p1.getReviewCount();
    }
 
}