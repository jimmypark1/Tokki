package com.Whowant.Tokki.VO;

import java.io.Serializable;

public class MarketMsg implements Serializable {

    private String name;
    private String profile;

    private String senderID;
    private String msg;

    private String date;
    private String cover;
    private String title;
    private String threadID;


    public MarketMsg() {

    }

    public void setName(String name){
        this.name = name;
    }
    public void setProfile(String profile){
        this.profile = profile;

    }
    public void setSenderId(String senderId){
        this.senderID = senderId;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }
    public void setDate(String date){
        this.date = date;
    }

    public void setCover(String cover){
        this.cover = cover;

    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setThreadId(String threadId)
    {
        this.threadID = threadId;
    }

    public String getName(){
        return name;

    }
    public String getProfile(){
        return profile;

    }
    public String getSenderID(){
        return senderID;

    }
    public String getMsg(){
        return msg;

    }
    public String getDate(){
        return date;

    }
    public String getCover(){
        return cover;

    }
    public String getTitle(){
        return title;

    }
    public String getThreadID(){
        return threadID;

    }
}