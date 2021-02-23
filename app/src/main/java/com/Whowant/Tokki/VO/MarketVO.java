package com.Whowant.Tokki.VO;



public class MarketVO {

    private String title;
    private String cover;

    private String nameAndGenre;
    private int status;
    private String tag;
    private String copywright0;
    private String copywright1;
    private int field;
    private String fieldName;
    private int price;
    private String career;
    private String workId;
    private String synopsis;
    private String name;
    private String genre;
    private String writerId;
    private String userId;

    public MarketVO() {

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


    public String getTitle(){ return title;}
    public String getCover(){ return cover;}

    public String getNameAndGenre(){ return nameAndGenre;}
    public int getPrice(){ return price;}
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
