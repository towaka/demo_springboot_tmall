package com.springboot.tmall.service;

import com.springboot.tmall.dao.ProductDAO;
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired ProductDAO productDAO;
    @Autowired CategoryService categoryService;
    @Autowired ProductImageService productImageService;
    @Autowired OrderItemService orderItemService;
    @Autowired CommentService commentService;


    public void add(Product bean){
        productDAO.save(bean);
    }

    public void delete(int id){
        productDAO.delete(id);
    }

    public Product get(int id){
        return productDAO.findOne(id);
    }

    public void update(Product bean){
        productDAO.save(bean);
    }

    public Page4Navigator<Product> list(int cid,int start,int size,int navigatePages){
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //以下方法遵循依赖顺序 ↓ ↓ ↓ ↓

    /**
     * 遍历某个分类获得其下的产品
     * @param category
     * @return
     */
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }

    /**
     * 给某分类下的产品集合设置首图
     * 保存经遍历此分类下的产品集合
     * @param category
     */
    public void fill(Category category) {
        List<Product> products = listByCategory(category);
        productImageService.setFirstProductImages(products);
        category.setProducts(products);
    }

    /**
     * 对fill(Category category)方法进行批量操作
     * @param categorys
     */
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }

    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                //注意subList只是做出了视图级别的操作，并没有影响集合内的数据
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    /**
     * 统计销量要从“已经结算”的订单项里结算，还在购物车里的产品不算进销量中
     * @param product
     */
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int commentCount = commentService.getCount(product);
        product.setCommentCount(commentCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    public List<Product> search(String keyword, int start, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products =productDAO.findByNameLike("%"+keyword+"%",pageable);
        return products;
    }

}
