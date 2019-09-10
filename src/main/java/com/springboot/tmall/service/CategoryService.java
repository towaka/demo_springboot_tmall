package com.springboot.tmall.service;

import com.springboot.tmall.dao.CategoryDAO;
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 这里抛弃了 XxxService 接口 加上 XxxServiceImpl 实现类的这种累赘的写法，
 * 而是直接使用 CategoryService 作为实现类来做
 */
@Service
public class CategoryService {
    @Autowired CategoryDAO categoryDAO;


    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    public Page4Navigator<Category> list(int start,int size,int navigatePages){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        //分页请求，
        Pageable pageable = new PageRequest(start,size,sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    public void delete(int id) {
        categoryDAO.delete(id);
    }

    public Category get(int id) {
        Category c= categoryDAO.findOne(id);
        return c;
    }

    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    /**
     * 以下两个方法必须要加<br>
     * 如果不加就会产生无限递归问题（Infinite Recursion）<br>
     * 原因可以看OrderService的removeOrderFromOrderItem方法<br>
     * {@link OrderService#removeOrderFromOrderItem(Order) }
     */
    public void removeCategoryFromProduct(Category category) {
        List<Product> products =category.getProducts();
        if(null!=products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow =category.getProductsByRow();
        if(null!=productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p: ps) {
                    p.setCategory(null);
                }
            }
        }
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }
}
