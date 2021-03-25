package com.Whowant.Tokki.VO;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.formula.functions.T;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public  class MessageThreadVO  {
    private int threadID;
    private String partnerID;
    private String userID;
    private String userName;
    private String userPhoto;
    private String createdDate;
    private String lastMsg;

    public int getThreadID() {
        return threadID;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    public String getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(String partnerID) {
        this.partnerID = partnerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }


}
