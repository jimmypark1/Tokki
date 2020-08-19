package com.Whowant.Tokki.VO;

import android.net.Uri;

public class CharacterVO {
    private String strName;
    private Uri imgUri;
    private String strImgFile;
    private int    nDirection;              // 0 = 왼쪽, 1 = 오른쪽
    private String strBalloonColor;            // 말풍선 컬러값
    private boolean bBlackText;              // 글씨 색상. true 라면 검은색, false 라면 흰색 글씨
    private boolean bBlackName;             // 이름 색상. true 라면 검은색, false 라면 흰색 이름
    private int    nIndex;                  // 작성창에서 몇번째인지
    private int    nCharacterID;
    private boolean bFaceShow = true;

    public CharacterVO() {

    }

    public void setnCharacterID(int nCharacterID) {
        this.nCharacterID = nCharacterID;
    }

    public void setName(String name) {
        strName = name;
    }

    public void setImage(Uri uri) {
        imgUri = uri;
    }

    public void setStrImgFile(String strImgFile) {
        this.strImgFile = strImgFile;
    }

    public void setDirection(int direction) {
        nDirection = direction;
    }

    public String getName() {
        return strName;
    }

    public Uri getImage() {
        return imgUri;
    }

    public int getDirection() {
        return nDirection;
    }

    public void setIndex(int index) {
        nIndex = index;
    }

    public int getIndex() {
        return nIndex;
    }

    public String getStrImgFile() {
        return strImgFile;
    }

    public int getnCharacterID() {
        return nCharacterID;
    }

    public String getStrBalloonColor() {
        return strBalloonColor;
    }

    public void setStrBalloonColor(String strBalloonColor) {
        this.strBalloonColor = strBalloonColor;
    }

    public boolean isbBlackText() {
        return bBlackText;
    }

    public void setbBlackText(boolean bBlackText) {
        this.bBlackText = bBlackText;
    }

    public boolean isbFaceShow() {
        return bFaceShow;
    }

    public void setbFaceShow(boolean bFaceShow) {
        this.bFaceShow = bFaceShow;
    }

    public boolean isbBlackName() {
        return bBlackName;
    }

    public void setbBlackName(boolean bBlackName) {
        this.bBlackName = bBlackName;
    }
}

