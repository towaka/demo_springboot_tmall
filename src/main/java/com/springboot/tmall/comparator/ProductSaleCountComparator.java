package com.springboot.tmall.comparator;

import com.springboot.tmall.pojo.Product;

import java.util.Comparator;

/**
 * 产品按销量降序排列
 */
public class ProductSaleCountComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount()-p1.getSaleCount();
    }
 
}