package com.springboot.tmall.service;

import com.springboot.tmall.dao.ProductDAO;
import com.springboot.tmall.es.ProductESDAO;
import com.springboot.tmall.pojo.Category;
import com.springboot.tmall.pojo.Product;
import com.springboot.tmall.util.Page4Navigator;
import com.springboot.tmall.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
@CacheConfig(cacheNames="products")
public class ProductService  {

    @Autowired
    ProductDAO productDAO;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CommentService commentService;

    @Autowired
    ProductESDAO productESDAO;//es支持

    @CacheEvict(allEntries=true)
    public void add(Product bean) {
        productDAO.save(bean);//同步到数据库
        productESDAO.save(bean);//同步到es
    }

    @CacheEvict(allEntries=true)
    public void delete(int id) {
        productDAO.delete(id);
        productESDAO.delete(id);
    }

    @Cacheable(key="'products-one-'+ #p0")
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    @CacheEvict(allEntries=true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }

    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }

    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProductImages(products);
        category.setProducts(products);
    }

    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = commentService.getCount(product);
        product.setCommentCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    /**
     * 修改查询方法
     * 以前查询是模糊查询，现在通过 ProductESDAO 到 elasticsearch 中进行查询了
     * 步骤：
     * 1.先创建分页参数，然后用 FunctionScoreQueryBuilder 定义 Function Score Query，并设置对应字段的权重分值。
     * 2.确定排序模式
     * 3.设定分页参数
     * 4.创建搜索查询
     * @param keyword
     * @param start
     * @param size
     * @return
     */
    public List<Product> search(String keyword, int start, int size) {
        /*
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products =productDAO.findByNameLike("%"+keyword+"%",pageable);
        return products;
        */
        //先尝试把数据同步到es中再说
        initDatabaseToES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                //查询条件，matchPhraseQuery会匹配单个字段
                .add(QueryBuilders.matchPhraseQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(100))//设置权重分
                .scoreMode("sum")//设置成求和模式
                .setMinScore(10);//设置权重分最低分
        //确定排序模式
        Sort sort  = new Sort(Sort.Direction.DESC,"id");
        //分页参数
        Pageable pageable = new PageRequest(start, size,sort);
        //创建搜索查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();
    }

    /**
     * 初始化数据到es. 因为数据刚开始都在数据库中，不在es中<br>
     * 所以先开始查询。先看看es有没有数据，如果没有，就把数据从数据库同步到es中<br>
     */
    private void initDatabaseToES() {
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page =productESDAO.findAll(pageable);//先在es查
        if(page.getContent().isEmpty()) {
            List<Product> products= productDAO.findAll();//没有再在数据库查
            for (Product product : products) {
                productESDAO.save(product);//再把数据同步到es
            }
        }
    }

}