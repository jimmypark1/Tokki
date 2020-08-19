package com.Whowant.Tokki.VO;

public class BillingLogVO {
    private int nBillID;
    private int nCoinPrice;
    private String strPurchaseDate;
    private String strOrderID;

    public BillingLogVO() {
    }

    public int getnBillID() {
        return nBillID;
    }

    public void setnBillID(int nBillID) {
        this.nBillID = nBillID;
    }

    public int getnCoinPrice() {
        return nCoinPrice;
    }

    public void setnCoinPrice(int nCoinPrice) {
        this.nCoinPrice = nCoinPrice;
    }

    public String getStrPurchaseDate() {
        return strPurchaseDate;
    }

    public void setStrPurchaseDate(String strPurchaseDate) {
        this.strPurchaseDate = strPurchaseDate;
    }

    public String getStrOrderID() {
        return strOrderID;
    }

    public void setStrOrderID(String strOrderID) {
        this.strOrderID = strOrderID;
    }
}
