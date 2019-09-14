package com.springboot.tmall.service;

import com.springboot.tmall.dao.OrderItemDAO;
import com.springboot.tmall.pojo.Order;
import com.springboot.tmall.pojo.OrderItem;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.pojo.User;
import com.springboot.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="orderItems")
public class OrderItemService {
	@Autowired OrderItemDAO orderItemDAO;
	@Autowired ProductImageService productImageService;

	@CacheEvict(allEntries=true)
	public void add(OrderItem orderItem){
		orderItemDAO.save(orderItem);
	}

	@CacheEvict(allEntries=true)
	public void delete(int id) {
		orderItemDAO.delete(id);
	}

	@CacheEvict(allEntries=true)
    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

	@Cacheable(key="'orderItems-one-'+ #p0")
	public OrderItem get(int id) {
		return orderItemDAO.findOne(id);
	}

	@Cacheable(key="'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

	@Cacheable(key="'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

	@Cacheable(key="'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }

	public void fill(List<Order> orders) {
		for (Order order : orders) 
			fill(order);
	}

    public void fill(Order order) {
		OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);//改成通过SpringContextUtil获取本类实例
		List<OrderItem> orderItems = orderItemService.listByOrder(order);
		float total = 0;
		int totalNumber = 0;			
		for (OrderItem oi :orderItems) {
			total+=oi.getNumber()*oi.getProduct().getPromotePrice();
			totalNumber+=oi.getNumber();
			productImageService.setFirstProductImage(oi.getProduct());
		}
		order.setTotal(total);
		order.setOrderItems(orderItems);
		order.setTotalNumber(totalNumber);		
	}

    /**
     * 统计指定商品在已经结算的订单项里的数量
     *
     * @param product
     * @return
     */
    public int getSaleCount(Product product) {
    	OrderItemService orderItemService = SpringContextUtil.getBean(OrderItemService.class);//改成通过SpringContextUtil获取本类实例
		List<OrderItem> ois =orderItemService.listByProduct(product);
        int result =0;
        for (OrderItem oi : ois) {
            if(null!=oi.getOrder())
                if(null!= oi.getOrder() && null!=oi.getOrder().getPayDate())
                    result+=oi.getNumber();
        }
        return result;
    }
}
