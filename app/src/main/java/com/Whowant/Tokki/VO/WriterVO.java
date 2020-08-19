package com.Whowant.Tokki.VO;

public class WriterVO {
    private String strWriterID;
    private String strWriterName;
    private String strWriterPhoto;
    private String strWriterComment;
    private int    nFollowcount;
    private int    nFollowingCount;

    public WriterVO() {

    }

    public String getStrWriterID() {
        return strWriterID;
    }

    public void setStrWriterID(String strWriterID) {
        this.strWriterID = strWriterID;
    }

    public String getStrWriterName() {
        return strWriterName;
    }

    public void setStrWriterName(String strWriterName) {
        this.strWriterName = strWriterName;
    }

    public String getStrWriterPhoto() {
        return strWriterPhoto;
    }

    public void setStrWriterPhoto(String strWriterPhoto) {
        this.strWriterPhoto = strWriterPhoto;
    }

    public String getStrWriterComment() {
        return strWriterComment;
    }

    public void setStrWriterComment(String strWriterComment) {
        this.strWriterComment = strWriterComment;
    }

    public int getnFollowcount() {
        return nFollowcount;
    }

    public void setnFollowcount(int nFollowcount) {
        this.nFollowcount = nFollowcount;
    }

    public int getnFollowingCount() {
        return nFollowingCount;
    }

    public void setnFollowingCount(int nFollowingCount) {
        this.nFollowingCount = nFollowingCount;
    }
}
