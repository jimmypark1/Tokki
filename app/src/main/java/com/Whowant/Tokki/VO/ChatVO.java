package com.Whowant.Tokki.VO;

import android.net.Uri;

public class ChatVO {                                     // 개개별의 말풍선
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_NARRATION = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_SOUND = 5;
    public static final int TYPE_CHANGE_BG_COLOR = 6;
    public static final int TYPE_DISTRACTOR = 7;
    public static final int TYPE_CHANGE_BG = 8;
    public static final int TYPE_END_COMMENT = 10;
    public static final int TYPE_IMAGE_NAR = 11;            // 추가된 나레이션 이미지
    public static final int TYPE_VIDEO_NAR = 12;            // 추가된 나레이션 비디오
    public static final int TYPE_EMPTY = -1;

    private int    nEpisodeID;

    private int    nChatID;
    private CharacterVO characterVO;
    private String strContents;
    private Uri    contentsUri;                             //  로컬 파일 추가 시에
    private String strContentsFile;                         //  서버에 있는 파일일때
    private int    nType;
    private int    nOrder;
    private int    nCommentCount;
    private int    nInteractionNum = 0;                     //  인터렉션  0 - 공통, 1 - 선택지1, 2 - 선택지2
    private boolean bNoDelbtn = false;

    public ChatVO() {

    }

    public void setnChatID(int nChatID) {
        this.nChatID = nChatID;
    }

    public void setnOrder(int nOrder) {
        this.nOrder = nOrder;
    }

    public void setnEpisodeID(int nEpisodeID) {
        this.nEpisodeID = nEpisodeID;
    }

    public void setType(int nType) {
        this.nType = nType;
    }

    public void setContents(String strContents) {
        this.strContents = strContents;
    }

    public void setContentsUri(Uri uri) {
        contentsUri = uri;
    }

    public void setStrContentsFile(String strImgFile) {
        this.strContentsFile = strImgFile;
    }

    public int getnChatID() {
        return nChatID;
    }

    public void setCharacter(CharacterVO vo) {
        characterVO = vo;
    }

    public String getContents() {
        return strContents;
    }

    public Uri getContentsUri() {
        return contentsUri;
    }

    public String getStrContentsFile() {
        return strContentsFile;
    }

    public int getnEpisodeID() {
        return nEpisodeID;
    }

    public int getType() {
        return nType;
    }

    public CharacterVO getCharacterVO() {
        return characterVO;
    }

    public int getnOrder() {
        return nOrder;
    }

    public int getnCommentCount() {
        return nCommentCount;
    }

    public void setnCommentCount(int nCommentCount) {
        this.nCommentCount = nCommentCount;
    }

    public boolean isbNoDelbtn() {
        return bNoDelbtn;
    }

    public void setbNoDelbtn(boolean bNoDelbtn) {
        this.bNoDelbtn = bNoDelbtn;
    }

    public int getnInteractionNum() {
        return nInteractionNum;
    }

    public void setnInteractionNum(int nInteractionNum) {
        this.nInteractionNum = nInteractionNum;
    }
}
