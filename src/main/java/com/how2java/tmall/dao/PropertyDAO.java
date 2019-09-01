package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 在SSM环境下建立增删改查功能，可以在XML文件写上SQL语句
 * 而在springboot环境下则不然
 * JpaRepository有一个方便的功能，支持接口规范方法名查询。
 * 意思是如果在接口中定义的方法名符合它的命名规则，就可以不用写实现
 *
 * 具体命名规范和细节可参考如下网址
 * @see <a href="https://blog.csdn.net/yanxilou/article/details/83114654">Spring data JPA方法命名规则</a>
 * @see <a href="https://www.jianshu.com/p/9feb08234133">SpringBoot 之 JPA 详解</a>
 */
public interface PropertyDAO extends JpaRepository<Property,Integer>{
    /**
     * 注意看，这个是个接口，它是没有实现类的，至少我们是没有显式提供实现类。
     * 那么要进行条件查询，就是在方法名上面做文章。
     * 比如这里的findByCategory，就是基于Category进行查询，第二个参数传一个 Pageable ，就支持分页了。
     * 这就是JPA所谓的不用写 SQL语句。。。因为需要的信息都在方法名和参数里提供了。
     * @param category
     * @param pageable
     * @return
     */
    Page<Property> findByCategory(Category category, Pageable pageable);
}
