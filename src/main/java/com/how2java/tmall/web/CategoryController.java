package com.how2java.tmall.web;
 
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired CategoryService categoryService;

    /**
     * @RequestParam 用于修饰请求参数
     * 通过注解@RequestParam可以轻松的将URL中的参数绑定到处理函数方法的变量中
     *
     * @param start
     * @param size
     * @return
     * @throws Exception
     */
    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Category> page =categoryService.list(start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return page;
    }

    /**
     * 通过CategoryService 将新分类保存到数据库
     * 但图片信息不保存到数据库，而是保存在被持久化的对象里
     *
     * @param bean
     * @param image
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    /**
     * 没别的，先删除数据库记录，再删除对应的图片文件
     * 注解中的路径映射 ListCategory.html的 ajax 请求
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id")int id,HttpServletRequest request){
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath(("img/category")));
        File file = new File(imageFolder,id+".jpg");
        file.delete();
        return null;
    }

    /**
     * 接受上传图片，并保存到 img/category目录下
     * 文件名中加入新增分类信息在数据库的id
     * 如果目录不存在，需要创建
     * image.transferTo 进行文件复制
     * 调用ImageUtil的change2jpg 进行文件类型强制转换为 jpg格式
     * 保存图片
     * @param bean
     * @param image  MultipartFile这个类一般是用来接受前台传过来的文件
     * @param request
     * @throws IOException
     */
    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        //通过request获得上下文目录位置
        File imageFolder= new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,bean.getId()+".jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }
}