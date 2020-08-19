package com.Whowant.Tokki.VO;

import java.util.ArrayList;

public class EpisodeVO {                                   // 한 회차별 정보 VO
    private int    nEpisodeID;
    private String strTitle;
    private String strDate;
    private int    nOrder;
    private ArrayList<ChatVO> chatVOArrayList;
    private ArrayList<CharacterVO> characterVOArrayList;
    private boolean bDistractor = false;                    // 분기 설정 했는지 여부
    private int    nHitsCount;
    private int    nTapCount;                               // 탭 수
    private float  fStarPoint;
    private int    nCommentCount;
    private String strSubmit;                               // N - 제출안됨, W - 승인대기, Y - 게시됨(승인됨)

    public EpisodeVO() {

    }

    public void setnEpisodeID(int nID) {
        this.nEpisodeID = nID;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public  void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public void setnOrder(int nOrder) {
        this.nOrder = nOrder;
    }

    public int getnEpisodeID() {
        return nEpisodeID;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrDate() {
        return strDate;
    }

    public int getnOrder() {
        return nOrder;
    }

    public void setDetailContensList(ArrayList<ChatVO> detailContentsList) {
        this.chatVOArrayList = detailContentsList;
    }

    public void setCharacterVOArrayList(ArrayList<CharacterVO> characterVOArrayList) {
        this.characterVOArrayList = characterVOArrayList;
    }

    public void setDistractor(boolean bDistractor) {
        this.bDistractor = bDistractor;
    }

    public ArrayList<ChatVO> getDetailContentsList() {
        return this.chatVOArrayList;
    }

    public ArrayList<CharacterVO> getCharacterVOArrayList() {
        return characterVOArrayList;
    }

    public boolean getIsDistractor() {
        return bDistractor;
    }

    public int getnHitsCount() {
        return nHitsCount;
    }

    public void setnHitsCount(int nHitsCount) {
        this.nHitsCount = nHitsCount;
    }

    public float getfStarPoint() {
        return fStarPoint;
    }

    public void setfStarPoint(float fStarPoint) {
        this.fStarPoint = fStarPoint;
    }

    public int getnCommentCount() {
        return nCommentCount;
    }

    public void setnCommentCount(int nCommentCount) {
        this.nCommentCount = nCommentCount;
    }

    public String getStrSubmit() {
        return strSubmit;
    }

    public void setStrSubmit(String strSubmit) {
        this.strSubmit = strSubmit;
    }

    public int getnTapCount() {
        return nTapCount;
    }

    public void setnTapCount(int nTapCount) {
        this.nTapCount = nTapCount;
    }
}