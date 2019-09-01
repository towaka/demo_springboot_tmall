package com.how2java.tmall.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * 分类属性(可修改、可增加、可删除)
 *     ┗ 产品属性(从分类获得。可修改、不可增加、不可删除)
 *
 * PropertyValue（属性值）只能修改，不能增加和删除
 * 增加和删除必须在分类属性下操作
 */
@Entity
@Table(name = "propertyvalue")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "pid")
    private Product product;

    @ManyToOne
    @JoinColumn(name="ptid")
    private Property property;

    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
