package com.Whowant.Tokki.VO;

public class WaitingVO {
    private int nWorkID;
    private String strWorkTitle;
    private int nEpisodeID;
    private int nEpisodeOrder;
    private String strEpisodeTitle;
    private String strCratedDate;
    private String strUpdateDate;
    private String strCoverImg;
    private String writerName;
    private String strSynopsis;

    public WaitingVO() {

    }

    public int getnWorkID() {
        return nWorkID;
    }

    public void setnWorkID(int nWorkID) {
        this.nWorkID = nWorkID;
    }


    public String getStrWorkTitle() {
        return strWorkTitle;
    }

    public void setStrWorkTitle(String strWorkTitle) {
        this.strWorkTitle = strWorkTitle;
    }

    public int getnEpisodeID() {
        return nEpisodeID;
    }

    public void setnEpisodeID(int nEpisodeID) {
        this.nEpisodeID = nEpisodeID;
    }

    public String getStrEpisodeTitle() {
        return strEpisodeTitle;
    }

    public void setStrEpisodeTitle(String strEpisodeTitle) {
        this.strEpisodeTitle = strEpisodeTitle;
    }

    public String getStrCratedDate() {
        return strCratedDate;
    }

    public void setStrCratedDate(String strCratedDate) {
        this.strCratedDate = strCratedDate;
    }

    public String getStrCoverImg() {
        return strCoverImg;
    }

    public void setStrCoverImg(String strCoverImg) {
        this.strCoverImg = strCoverImg;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public int getnEpisodeOrder() {
        return nEpisodeOrder;
    }

    public void setnEpisodeOrder(int nEpisodeOrder) {
        this.nEpisodeOrder = nEpisodeOrder;
    }

    public String getStrSynopsis() {
        return strSynopsis;
    }

    public void setStrSynopsis(String strSynopsis) {
        this.strSynopsis = strSynopsis;
    }

    public String getStrUpdateDate() {
        return strUpdateDate;
    }

    public void setStrUpdateDate(String strUpdateDate) {
        this.strUpdateDate = strUpdateDate;
    }
}
