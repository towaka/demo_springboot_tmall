package com.springboot.tmall.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 配置类，用于允许所有的请求都跨域。
 * 因为请求次数起码有两次，一次是获取 html 页面， 然后是通过 html 页面上的 js 代码异步获取数据，
 * 一旦部署到服务器就容易面临跨域请求问题，所以允许所有访问都跨域，就不会出现通过 ajax 获取数据获取不到的问题了。
 *
 * 跨域的意思就是，比如 http://tc.com/a.js 这个脚本， 如果想在 td.com 的某个页面去调用， 这就是跨域了。
 * 跨域存在 a.com 是否允许别的域名访问的问题，就是这个概念啦。
 *
 * 目前版本为1.5.9.RELEASE
 * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
 * 但在2.0版本该类已经被注上@Deprecated的注解，注意版本问题 ，
 * 之前调用错了WebMvcConfigurationSupport类导致无法实现跨域
 */
@Configuration
public class CORSConfiguration extends WebMvcConfigurerAdapter{

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")//配置可以被跨域的路径，可以任意配置，可以具体到直接请求路径。
                .allowedOrigins("*")//允许所有的请求域名访问我们的跨域资源，可以固定单条或者多条内容。
                .allowedMethods("*")//允许所有的请求方法访问该跨域资源服务器，如：POST、GET、PUT、DELETE等。
                .allowedHeaders("*");//允许所有的请求header访问，也可以自定义设置任意请求头信息
    }
}
