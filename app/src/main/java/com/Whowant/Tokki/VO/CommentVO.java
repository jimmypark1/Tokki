package com.Whowant.Tokki.VO;

public class CommentVO {
    private int commentID;
    private int episodeID;
    private String strComment;
    private int parentID;
    private String registerDate;
    private String userName;
    private String userPhoto;
    private String userID;
    private int chatID;
    private int likeCount;
    private boolean myComment;
    private String strWorkTitle;
    private int nEpisodeOrder;
    private String strReportReson;
    private int    nReportID;
    private String strRepotUserName;
    private String strReportDate;
    private int    nCount;
    private int    nGroup;
    private boolean hasChild;
    private int    nDonationCarrot;

    public CommentVO() {

    }


    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getEpisodeID() {
        return episodeID;
    }

    public void setEpisodeID(int episodeID) {
        this.episodeID = episodeID;
    }

    public String getStrComment() {
        return strComment;
    }

    public void setStrComment(String strComment) {
        this.strComment = strComment;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
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

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isMyComment() {
        return myComment;
    }

    public void setMyComment(boolean myComment) {
        this.myComment = myComment;
    }

    public String getStrWorkTitle() {
        return strWorkTitle;
    }

    public void setStrWorkTitle(String strWorkTitle) {
        this.strWorkTitle = strWorkTitle;
    }

    public int getnEpisodeOrder() {
        return nEpisodeOrder;
    }

    public void setnEpisodeOrder(int nEpisodeOrder) {
        this.nEpisodeOrder = nEpisodeOrder;
    }

    public String getStrReportReson() {
        return strReportReson;
    }

    public void setStrReportReson(String strReportReson) {
        this.strReportReson = strReportReson;
    }

    public int getnReportID() {
        return nReportID;
    }

    public void setnReportID(int nReportID) {
        this.nReportID = nReportID;
    }

    public String getStrReportDate() {
        return strReportDate;
    }

    public void setStrReportDate(String strReportDate) {
        this.strReportDate = strReportDate;
    }

    public String getStrRepotUserName() {
        return strRepotUserName;
    }

    public void setStrRepotUserName(String strRepotUserName) {
        this.strRepotUserName = strRepotUserName;
    }

    public int getnCount() {
        return nCount;
    }

    public void setnCount(int nCount) {
        this.nCount = nCount;
    }

    public int getnGroup() {
        return nGroup;
    }

    public void setnGroup(int nGroup) {
        this.nGroup = nGroup;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public int getnDonationCarrot() {
        return nDonationCarrot;
    }

    public void setnDonationCarrot(int nDonationCarrot) {
        this.nDonationCarrot = nDonationCarrot;
    }
}
