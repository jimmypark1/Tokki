package com.Whowant.Tokki.VO;

public class AlarmVO {
    private int    nAlarmID;
    private String alarmTitle;
    private String alarmContents;
    private String strObjectID;
    private String strDateTime;
    private int    nAlarmType;
    private boolean bRead;

    public AlarmVO() {

    }

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public void setAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }

    public String getAlarmContents() {
        return alarmContents;
    }

    public void setAlarmContents(String alarmContents) {
        this.alarmContents = alarmContents;
    }

    public String getStrObjectID() {
        return strObjectID;
    }

    public void setStrObjectID(String strObjectID) {
        this.strObjectID = strObjectID;
    }

    public String getStrDateTime() {
        return strDateTime;
    }

    public void setStrDateTime(String strDateTime) {
        this.strDateTime = strDateTime;
    }

    public int getnAlarmID() {
        return nAlarmID;
    }

    public void setnAlarmID(int nAlarmID) {
        this.nAlarmID = nAlarmID;
    }

    public int getnAlarmType() {
        return nAlarmType;
    }

    public void setnAlarmType(int nAlarmType) {
        this.nAlarmType = nAlarmType;
    }

    public boolean isbRead() {
        return bRead;
    }

    public void setbRead(boolean bRead) {
        this.bRead = bRead;
    }
}
