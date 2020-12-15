package com.Whowant.Tokki.VO;

public class MessageVO {
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
