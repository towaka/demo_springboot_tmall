demo_springboot_tmall
====
本项目预期中的架构是springboot+mysql+shiro+redis+elasticsearch,前端是javascript+vue.js+thymeleaf+bootstrap,可以从package com.springboot.tmall.web 开始看里面的控制类跟踪业务实现

整个过程持续了几个月时间，之前一直都只在本地测试，前端是较大难点，一边做一边学习<br>

版本信息：
---

### springboot<br>

    1.springboot 1.5.9.RELEASE<br>

### redis <br>

    1.redis-client: redisclient-win32.x86.1.5<br>
    2.redis-server: redis-2.4.5-win32<br>

### shiro<br>

    1.org.apache.shiro-1.3.2<br>  

### Mysql<br>

    1.MySQL-Server-5.7<br> 
    2.MySQL-Front-6.1<br> 
    3.MySQL-connector-java-5.1.21<br>

### elasticsearch<br>

    1.elasticsearch-2.4.2<br> 
    2.kibana-4.6.3-windows-x86<br> 
    3.com.sun.jna-3.0.9<br>
    
### bootstrap<br>
    1.bootstrap-3.3.6<br>
    
### vue.js<br>
    1. vue.js-2.5.16<br>

### thymeleaf<br>
    1. thymeleaf legacyhtml5 模式支持-1.9.22<br>
    
### javascript<br>
    1.ECMAScript 5.1<br>
    
<br><br><br>
项目涉及9张表<br>
![](https://github.com/towaka/demo_springboot_tmall/blob/master/tables.png)<br>
<br><br><br>
表和表之间的关系（绘图工具：https://www.processon.com ）<br>
![](https://github.com/towaka/demo_springboot_tmall/blob/master/%E8%A1%A8%E5%85%B3%E7%B3%BB.png)<br>
<br><br><br>

每个表的用途<br>
![](https://github.com/towaka/demo_springboot_tmall/blob/master/%E8%A1%A8%E7%94%A8%E9%80%94.png)<br>
<br><br><br>




------更新进度------<br>
2019.9.15 <br>
添加elasticsearch支持 <br>

2019.9.14 <br>
加入对shiro和redis的支持<br>

2019.9.13<br>
更正订单页操作无响应和无法评论的错误<br>

2019.9.8 <br>
加入购物车数量增加、首页跳转、搜索栏下分类推荐<br>

2019.9.7<br>
上传产品页的参数、详情图片、价格显示等功能<br>

2019.9.6<br>
涉及表一览<br>

2019.9.5<br>
实现前台页面普通登录和退出功能<br>

2019-09-04<br>
增加对首页纵向和横向显示分类链接的支持<br>
增加对前台的页面跳转和服务等支持<br>

2019-09-03<br>
后台部分完成，但没有设置登陆功能<br>
