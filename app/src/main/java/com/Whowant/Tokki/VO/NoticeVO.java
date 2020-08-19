package com.Whowant.Tokki.VO;

public class NoticeVO {
    int nNoticeID;
    String strUserID;
    String strUserName;
    String strNoticeTitle;
    String strNoticeContents;
    String strRegisterDate;
    boolean bRead;
    boolean bExpand;

    public NoticeVO() {

    }

    public void setnNoticeID(int nNoticeID) {
        this.nNoticeID = nNoticeID;
    }

    public int getnNoticeID() {
        return nNoticeID;
    }

    public String getStrUserID() {
        return strUserID;
    }

    public void setStrUserID(String strUserID) {
        this.strUserID = strUserID;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    public String getStrNoticeTitle() {
        return strNoticeTitle;
    }

    public void setStrNoticeTitle(String strNoticeTitle) {
        this.strNoticeTitle = strNoticeTitle;
    }

    public String getStrNoticeContents() {
        return strNoticeContents;
    }

    public void setStrNoticeContents(String strNoticeContents) {
        this.strNoticeContents = strNoticeContents;
    }

    public String getStrRegisterDate() {
        return strRegisterDate;
    }

    public void setStrRegisterDate(String strRegisterDate) {
        this.strRegisterDate = strRegisterDate;
    }

    public void setbExpand(boolean bExpand) {
        this.bExpand = bExpand;
    }

    public boolean getbExpand() {
        return bExpand;
    }

    public void setbRead(boolean bRead) {
        this.bRead = bRead;
    }

    public boolean getbRead() {
        return bRead;
    }

}
