package com.springboot.tmall.service;

import com.springboot.tmall.dao.ProductImageDAO;
import com.springboot.tmall.pojo.OrderItem;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService   {

    @Autowired
    ProductImageDAO productImageDAO;
    @Autowired ProductService productService;

    public static final String type_single = "single";
    public static final String type_detail = "detail";

    public void add(ProductImage bean) {
        productImageDAO.save(bean);

    }
    public void delete(int id) {
        productImageDAO.delete(id);
    }

    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }

    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    public void setFirstProductImage(Product product) {
        List<ProductImage> singleImages = listSingleProductImages(product);
        if(!singleImages.isEmpty())
            product.setFirstProductImage(singleImages.get(0));
        else
            product.setFirstProductImage(new ProductImage());
    }

    public void setFirstProductImages(List<Product> products) {
       for(Product product:products){
           setFirstProductImage(product);
       }
    }

    /**
     * 给订单项填充产品的预览图片
     * @param ois
     */
    public void setFirstProductImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProductImage(orderItem.getProduct());
        }
    }

}
