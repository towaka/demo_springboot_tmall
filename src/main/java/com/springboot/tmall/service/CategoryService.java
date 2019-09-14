package com.springboot.tmall.service;
import java.util.List;
import com.springboot.tmall.dao.CategoryDAO;
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * 这里抛弃了 XxxService 接口 加上 XxxServiceImpl 实现类的这种累赘的写法，
 * 而是直接使用 CategoryService 作为实现类来做
 *
 * 添加了Redis缓存配置
 */
@Service
//@CacheConfig是一个类级别的注解
//表示分类在缓存里的keys，都是归 "categories" 这个管理的
//本项目里通过图形界面的Redis客户端可以看到有categories~keys，用于维护分类信息在 redis里都有哪些 key
@CacheConfig(cacheNames="categories")
public class CategoryService {
    @Autowired CategoryDAO categoryDAO;

    @CacheEvict(allEntries=true)
//  @CachePut(key="'category-one-'+ #p0")
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
//  @CacheEvict(key="'category-one-'+ #p0")
    public void delete(int id) {
        categoryDAO.delete(id);
    }

    @CacheEvict(allEntries=true)
//  @CachePut(key="'category-one-'+ #p0")
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    /**
     * 在结合redis缓存的项目中，第一次通过数据库访问数据时，redis里是不会有数据的，<br>
     * 但一旦被取出，就会放在redis里，<br>
     * 第二次访问时就无需经过数据库了<br>
     * <br>
     * 这里面#p0存放的是方法的形参，且注解里可以不止一个#p+序号，和方法形参顺位对应<br>
     * @param id
     * @return
     */
    @Cacheable(key="'categories-one-'+ #p0")
    public Category get(int id) {
        Category c= categoryDAO.findOne(id);
        return c;
    }

    @Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    @Cacheable(key="'categories-all'")
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    /**
     * 以下两个方法必须要加<br>
     * 如果不加就会产生无限递归问题（Infinite Recursion）<br>
     * 原因可以看OrderService的removeOrderFromOrderItem方法<br>
     * {@link OrderService#removeOrderFromOrderItem(Order) }
     *
     * @param cs
     */
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

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
}
