package com.Whowant.Tokki.VO;

public class ContestVO {            // 공모전
    private int    contestID;
    private int    workID;
    private String strTitle;
    private String userName;
    private String userID;
    private String userBirthday;
    private String email;
    private boolean isCareer;
    private String characterInfo;
    private String synopsis;
    private String contestCode;
    private String registerDate;
    private String strPhoneNum;

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCareer() {
        return isCareer;
    }

    public void setCareer(boolean career) {
        isCareer = career;
    }

    public String getCharacterInfo() {
        return characterInfo;
    }

    public void setCharacterInfo(String characterInfo) {
        this.characterInfo = characterInfo;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getContestCode() {
        return contestCode;
    }

    public void setContestCode(String contestCode) {
        this.contestCode = contestCode;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public int getContestID() {
        return contestID;
    }

    public void setContestID(int contestID) {
        this.contestID = contestID;
    }

    public int getWorkID() {
        return workID;
    }

    public void setWorkID(int workID) {
        this.workID = workID;
    }

    public String getStrPhoneNum() {
        return strPhoneNum;
    }

    public void setStrPhoneNum(String strPhoneNum) {
        this.strPhoneNum = strPhoneNum;
    }

    /*
    contestInfo.put("USER_ID", pref.getString("USER_ID", "Guest"));
    contestInfo.put("USER_NAME", inputNameView.getText().toString());
    contestInfo.put("USER_BIRTHDAY", strBirthday);
    contestInfo.put("USER_PHONENUM", inputPhoneNumView.getText().toString());
    contestInfo.put("EMAIL", inputEmailView.getText().toString());
    contestInfo.put("CAREER", bCheck == true ? "Y" : "N");
    contestInfo.put("WORK_TITLE", strWorkTitle);
    contestInfo.put("CHARACTER_INFO", inputCharacterView.getText().toString());
    contestInfo.put("SYNOPSIS", inputSynopsisView.getText().toString());
     */
}
