package com.houjun.redission;

public class Product {

    /**
     * 商品唯一Id
     */
    private String id;

    /**
     * 库存
     */
    private Long stock = 2L;  //方便测试，请求到第三次时应该为购买失败

    public Product(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(final Long stock) {
        this.stock = stock;
    }
}