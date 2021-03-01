package com.Whowant.Tokki.VO;

public class CarrotItemVO implements Comparable<CarrotItemVO>{

    private String desc;
    private String productId;
    private int price;
    private String strPrice;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStrPrice(String strPrice) {
        this.strPrice = strPrice;
    }

    public String getStrPrice() {
        return strPrice;
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

    @Override
    public int compareTo(CarrotItemVO s) {
        if (this.getPrice() < s.getPrice()) {
            return -1;
        } else if (this.getPrice() > s.getPrice()) {
            return 1;
        }
        return 0;
    }


}
