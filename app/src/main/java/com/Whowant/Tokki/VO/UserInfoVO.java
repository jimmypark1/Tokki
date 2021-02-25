package com.Whowant.Tokki.VO;

public class UserInfoVO {
    private String strUserID;
    private String strUserName;
    private String strUserEmail;
    private String strUserPhoto;
    private int    nWorkCount;
    private int    nCommentCount;
    private int    nFollowerCount;
    private int    nAccumCarrot;
    private int    nDonationCarrot;
    private int    nPurchaseCarrot;

    private int    type;
    private String comment;
    private String background;
    private String birthday;
    private int gender;
    private String phone;


    public UserInfoVO() {

    }


    public void setBackground(String background)
    {
        this.background = background;
    }
    public String getBackground()
    {
        return background;
    }
    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public String getBirthday()
    {
        return birthday;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
    public void setGender(int gender){
        this.gender = gender;
    }
    public int getGender()
    {
        return gender;
    }

    public void setType(int type){ this.type = type;}
    public int getType(){
        return type;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getStrUserEmail() {
        return strUserEmail;
    }

    public void setStrUserEmail(String strUserEmail) {
        this.strUserEmail = strUserEmail;
    }

    public String getStrUserPhoto() {
        return strUserPhoto;
    }

    public void setStrUserPhoto(String strUserPhoeo) {
        this.strUserPhoto = strUserPhoeo;
    }

    public int getnWorkCount() {
        return nWorkCount;
    }

    public void setnWorkCount(int nWorkCount) {
        this.nWorkCount = nWorkCount;
    }

    public int getnCommentCount() {
        return nCommentCount;
    }

    public void setnCommentCount(int nCommentCount) {
        this.nCommentCount = nCommentCount;
    }

    public int getnFollowerCount() {
        return nFollowerCount;
    }

    public void setnFollowerCount(int nFollowerCount) {
        this.nFollowerCount = nFollowerCount;
    }

    public int getnAccumCarrot() {
        return nAccumCarrot;
    }

    public void setnAccumCarrot(int nAccumCarrot) {
        this.nAccumCarrot = nAccumCarrot;
    }

    public int getnDonationCarrot() {
        return nDonationCarrot;
    }

    public void setnDonationCarrot(int nDonationCarrot) {
        this.nDonationCarrot = nDonationCarrot;
    }

    public int getnPurchaseCarrot() {
        return nPurchaseCarrot;
    }

    public void setnPurchaseCarrot(int nPurchaseCarrot) {
        this.nPurchaseCarrot = nPurchaseCarrot;
    }
}
