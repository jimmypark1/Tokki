package com.Whowant.Tokki.VO;


import java.io.Serializable;

public class MarketVO implements Serializable {

    private String title;
    private String cover;

    private String nameAndGenre;
    private int status;
    private String tag;
    private String copywright0;
    private String copywright1;
    private int field;
    private String fieldName;
    private long price;
    private String career;
    private String workId;
    private int workType;
    private String synopsis;
    private String name;
    private String genre;
    private String writerId;
    private String userId;
    private String strField;
    private String strGenre;
    private String strTag;
    private int transactionPrice;
    private String hopeTag;
    private String hopeGenre;

    private int marketId;


    public MarketVO() {

    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setWorkType(int workType) {
        this.workType = workType;
    }

    public int getWorkType() {
        return workType;
    }

    public void setHopeGenre(String hopeGenre) {
        this.hopeGenre = hopeGenre;
    }

    public void setHopeTag(String hopeTag) {
        this.hopeTag = hopeTag;
    }

    public String getHopeGenre() {
        return hopeGenre;
    }

    public String getHopeTag() {
        return hopeTag;
    }

    public void setTransactionPrice(int transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public int getTransactionPrice() {
        return transactionPrice;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setStrGenre(String strGenre) {
        this.strGenre = strGenre;
    }

    public void setStrTag(String strTag) {
        this.strTag = strTag;
    }

    public void setCopywright0(String copywright0) {
        this.copywright0 = copywright0;
    }

    public void setCopywright1(String copywright1) {
        this.copywright1 = copywright1;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public String getCopywright0() {
        return copywright0;
    }

    public String getCopywright1() {
        return copywright1;
    }

    public String getStrField() {
        return strField;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getWriterId() {
        return writerId;
    }


    public void setTitle(String title) {
        this.title = title;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public void setUserId(String cover) {
        this.userId = userId;
    }

    public void setNameAndGenre(String nameAndGenre) {
        this.nameAndGenre = nameAndGenre;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setField(int field) {
        this.field = field;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setCopyright0(String copywright0) {
        this.copywright0 = copywright0;
    }
    public void setCopyright1(String copywright1) {
        this.copywright1 = copywright1;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public void setCareer(String career) {
        this.career = career;
    }
    public void setWorkId(String workId) {
        this.workId = workId;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }


    public String getStrGenre() {
        return strGenre;
    }

    public String getStrTag() {
        return strTag;
    }

    public String getTitle(){ return title;}
    public String getCover(){ return cover;}

    public String getNameAndGenre(){ return nameAndGenre;}
    public long getPrice(){ return price;}
    public int getField(){ return field;}
    public int getStatus(){ return status;}
    public String getTag(){ return tag;}
    public String getCopyright0(){ return copywright0;}
    public String getCopyright1(){ return copywright1;}
    public String getFieldName(){ return fieldName;}
    public String getCareer(){ return career;}
    public String getWorkId(){ return workId;}
    public String getSypnopsis(){ return synopsis;}
    public String getName(){ return name;}
    public String getGenre(){ return genre;}
    public String getWriteId(){ return writerId;}
    public String getUserId(){ return userId;}


}
