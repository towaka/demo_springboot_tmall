package com.springboot.tmall;

import com.springboot.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//用于启动缓存,但是使用缓存功能，一来需要在配置文件设置，二来要在服务层的类里进行注解
@EnableCaching
//在Application中，需要为es和jpa分别指定不同的包名，否则会出错
@EnableElasticsearchRepositories(basePackages = "com.springboot.tmall.es")
@EnableJpaRepositories(basePackages = {"com.springboot.tmall.dao", "com.springboot.tmall.pojo"})
public class Application {
    /**
     *  检查端口6379是否启动。6379就是Redis服务器使用的端口。如果未启动那么就会退出springboot。
     */
    static{
        PortUtil.checkPort(6379,"Redis服务端",true);
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}