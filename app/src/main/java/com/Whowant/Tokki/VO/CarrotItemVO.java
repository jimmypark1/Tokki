package com.Whowant.Tokki.VO;

public class CarrotItemVO {

    private String desc;
    private String productId;
    private int price;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getPrice() {
        return price;
    }

    public String getDesc() {
        return desc;
    }

    public String getProductId() {
        return productId;
    }
}
