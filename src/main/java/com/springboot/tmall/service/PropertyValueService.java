package com.springboot.tmall.service;
 
import com.springboot.tmall.dao.PropertyValueDAO;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.pojo.Property;
import com.springboot.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;

/**
 * PropertyValue（属性值）只用于修改属性，不能增加和删除
 * 增加和删除属性必须在分类属性下操作
 */
@Service
public class PropertyValueService  {
 
    @Autowired PropertyValueDAO propertyValueDAO;
    @Autowired PropertyService propertyService;
 
    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }
 
    public void init(Product product) {
        //首先通过产品所属的分类来获取属性集合（分类的属性值）
        List<Property> propertys= propertyService.listByCategory(product.getCategory());
        //然后再通过产品本身来获取产品自己的属性“值”集合
        for (Property property: propertys) {
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            //如果在遍历下这个产品的这个属性还没有值
            if(null==propertyValue){
                propertyValue = new PropertyValue();//初始化一个PropertyValue，并输入值
                propertyValue.setProduct(product);//设置这个值所属哪个产品
                propertyValue.setProperty(property);//设置这个值属于哪个属性
                propertyValueDAO.save(propertyValue);//把属性值保存在数据库
            }
        }
    }
 
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }
 
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }
     
}