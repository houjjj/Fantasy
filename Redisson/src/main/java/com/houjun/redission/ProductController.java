package com.houjun.redission;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private static List<Product> products = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * 初始化数据
     */
    @PostConstruct
    private void init() {
        products.add(new Product("1"));
        products.add(new Product("2"));
    }

    @GetMapping("/purchase")
    @ResponseBody
    public String purchase() {

        //获取分布式锁
        RLock transferLock = redissonClient.getLock("PURCHASE");

        transferLock.lock();
        //业务逻辑卸载try...catch中 ，finally最后一定要释放锁
        try {
            //尝试获取锁
            Product product = findById("1"); //为了方便测试，直接写死，实际商品代码应有用户post
            if (product.getStock() < 1) {
                return "商品已经卖完啦！！！";
            }

            product.setStock(product.getStock() - 1);
            updateProduct(product);
            return "商品购买成功！！！";

        } catch (Exception e) {
            logger.error("",e);
        } finally {
            // 无论是否出现异常，一定解锁
            transferLock.unlock();
        }

        return "商品购买失败";

    }

    /**
     * 根据id查询商品
     *
     * @param id 唯一id
     * @return Product
     */
    private Product findById(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    private Product updateProduct(Product product) {

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.set(i, product);
                return product;
            }
        }
        return null;
    }

}