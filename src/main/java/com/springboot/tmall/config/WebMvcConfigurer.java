package com.springboot.tmall.config;

import com.springboot.tmall.interceptor.GeneralInterceptor;
import com.springboot.tmall.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 配置拦截器的东西
 * 该@Bean注释被用于指示一个方法实例，配置和初始化为通过Spring IoC容器进行管理的新对象。<br>
 * 对于那些熟悉Spring的<beans/>XML配置的人来说，@Bean注释与<bean/>元素扮演的角色相同，<br/>
 * 但它们最常用于@Configuration豆类。<br/>
 *
 * 结合本项目环境，本类getLoginInterceptor()的配置等效于以下xml代码：<br/>
 * <beans>
 *     <bean id="getLoginInterceptor" class="com.springboot.tmall.config.WebMvcConfigurer"/>
 * </beans>
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {
    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Bean
    public GeneralInterceptor getGeneralInterceptor(){
        return new GeneralInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(getGeneralInterceptor()).addPathPatterns("/**");
    }
}
