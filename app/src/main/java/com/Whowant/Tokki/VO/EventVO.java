package com.Whowant.Tokki.VO;

public class EventVO {
    int nEventID;
    int nEventType;
    String strUserID;
    String strUserName;
    String strEventTitle;
    String strEventContentsFile;
    String strEventPopupFile;
    String strRegisterDate;
    private boolean bRead;

    public EventVO() {

    }

    public void setnEventID(int nEventID) {
        this.nEventID = nEventID;
    }

    public int getnEventID() {
        return nEventID;
    }

    public void setnEventType(int nEventType) {
        this.nEventType = nEventType;
    }

    public int getnEventType() {
        return nEventType;
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

    public String getStrEventTitle() {
        return strEventTitle;
    }

    public void setStrEventTitle(String strEventTitle) {
        this.strEventTitle = strEventTitle;
    }

    public String getStrEventContentsFile() {
        return strEventContentsFile;
    }

    public void setStrEventPopupFile(String strEventPopupFile) {
        this.strEventPopupFile = strEventPopupFile;
    }

    public String getStrEventPopupFile() {
        return strEventPopupFile;
    }

    public void setStrEventContentsFile(String strEventContentsFile) {
        this.strEventContentsFile = strEventContentsFile;
    }

    public String getStrRegisterDate() {
        return strRegisterDate;
    }

    public void setStrRegisterDate(String strRegisterDate) {
        this.strRegisterDate = strRegisterDate;
    }

    public boolean isbRead() {
        return bRead;
    }

    public void setbRead(boolean bRead) {
        this.bRead = bRead;
    }
}
