package com.springboot.tmall.es;

import com.springboot.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Springboot提供了对ElasticSearch专门的jpa的，叫做ElasticsearchRepository
 * version 2.4.2
 * 截止到目前为止最新版本为7.3.2（September 12, 2019）
 * 因为本项目springboot版本为1.5.9，ElasticsearchRepository没法完美支持最新版本的ElasticSearch
 *
 * 注意，需要和DAO包做分开出来，另开一个包
 */
public interface ProductESDAO extends ElasticsearchRepository<Product,Integer> {
}
