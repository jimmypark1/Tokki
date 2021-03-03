package com.Whowant.Tokki.VO;

import java.io.Serializable;

public class MessageVO implements Serializable {
    private int threadID;
    private int messageID;
    private String receiverID;
    private String receiverName;
    private String receiverPhoto;
    private String senderID;
    private String senderName;
    private String senderPhoto;
    private String msgContents;
    private String createdDate;
    private boolean isUnread;
    private String readDate;
    private int carrot;
    private String contract_complete;
    private int type;


    public void setCarrot(int carrot) {
        this.carrot = carrot;
    }

    public void setContract_complete(String contract_complete) {
        this.contract_complete = contract_complete;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCarrot() {
        return carrot;
    }

    public int getType() {
        return type;
    }

    public String getContract_complete() {
        return contract_complete;
    }

    public int getThreadID() {
        return threadID;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMsgContents() {
        return msgContents;
    }

    public void setMsgContents(String msgContents) {
        this.msgContents = msgContents;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getReadDate() {
        return readDate;
    }

    public void setReadDate(String readDate) {
        this.readDate = readDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhoto() {
        return receiverPhoto;
    }

    public void setReceiverPhoto(String receiverPhoto) {
        this.receiverPhoto = receiverPhoto;
    }

    public String getSenderPhoto() {
        return senderPhoto;
    }

    public void setSenderPhoto(String senderPhoto) {
        this.senderPhoto = senderPhoto;
    }
}
