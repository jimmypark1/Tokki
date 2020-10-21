package com.Whowant.Tokki.VO;

import java.util.ArrayList;

public class WorkVO {
    private ArrayList<EpisodeVO> episodeList;
    private int    nWorkID;                                 // 작품 ID
    private String strWriterID;                               // 작가 ID
    private String strWriterName;                           // 작가 이름
    private String strWriterPhoto;
    private String strTitle;                                // 작품 제목
    private String strSynopsis;                             // 작품 줄거리
    private String strCoverFile;                            // 커버 이미지 파일명
    private String strThumbFile;                            // 커버 써멘일 파일명
    private String strCreatedDate;                          // 작품 생성일
    private String strUpdateDate;                           // 작품 수정읾
    private int    nHitsCount;
    private int    nTapCount;                               // 탭 수
    private float  fStarPoint;
    private int    nKeepcount;                              // 구독 수
    private int    nCommentCount;
    private int    nTarget;                                 // 구독자 층
    private boolean bComplete;                              // 완결 여부
    private int    nInteractionEpisodeID;                   // 분기된 에피소드 아이디
    private int    nDonationCarrot;                         // 응원 받은 당근
    private boolean bPosterThumbnail;

    private boolean bDistractor = false;                    // 분기 설정 했는지 여부
    private int     nUserStatus = 1;                        // 사용자 상태. 1 : 일반유저, 99 : 탈퇴된 유저, 10 : black list, 20 : white list

    public WorkVO() {

    }

    public void setStrWriterName(String strWriterName) {
        this.strWriterName = strWriterName;
    }

    public void setWorkID(int nWorkID) {
        this.nWorkID = nWorkID;
    }

    public void setWriteID(String strWriterID) {
        this.strWriterID = strWriterID;
    }

    public void setTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setStrSynopsis(String strSynopsis) {
        this.strSynopsis = strSynopsis;
    }

    public void setCoverFile(String strCoverFile) {
        this.strCoverFile = strCoverFile;
    }

    public void setCreatedDate(String strCreatedDate) {
        this.strCreatedDate = strCreatedDate;
    }

    public void setEpisodeList(ArrayList<EpisodeVO> episodeList) {
        this.episodeList = episodeList;
    }

    public int getnWorkID() {
        return nWorkID;
    }

    public String  getnWriterID() {
        return strWriterID;
    }

    public String getTitle() {
        return strTitle;
    }

    public String getStrWriterName() {
        return strWriterName;
    }

    public String getSynopsis() {
        return strSynopsis;
    }

    public String getCoverFile() {
        return strCoverFile;
    }

    public String getCreatedDate() {
        return strCreatedDate;
    }

    public ArrayList<EpisodeVO> getEpisodeList() {
        return episodeList;
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

    public boolean isbComplete() {
        return bComplete;
    }

    public void setbComplete(boolean bComplete) {
        this.bComplete = bComplete;
    }

    public int getnCommentCount() {
        return nCommentCount;
    }

    public void setnCommentCount(int nCommentCount) {
        this.nCommentCount = nCommentCount;
    }

    public String getStrWriterPhoto() {
        return strWriterPhoto;
    }

    public void setStrWriterPhoto(String strWriterPhoto) {
        this.strWriterPhoto = strWriterPhoto;
    }

    public int getnKeepcount() {
        return nKeepcount;
    }

    public void setnKeepcount(int nKeepcount) {
        this.nKeepcount = nKeepcount;
    }

    public boolean isbDistractor() {
        return bDistractor;
    }

    public void setbDistractor(boolean bDistractor) {
        this.bDistractor = bDistractor;
    }

    public int getnTarget() {
        return nTarget;
    }

    public void setnTarget(int nTarget) {
        this.nTarget = nTarget;
    }

    public int getnTapCount() {
        return nTapCount;
    }

    public void setnTapCount(int nTapCount) {
        this.nTapCount = nTapCount;
    }

    public int getnInteractionEpisodeID() {
        return nInteractionEpisodeID;
    }

    public void setnInteractionEpisodeID(int nInteractionEpisodeID) {
        this.nInteractionEpisodeID = nInteractionEpisodeID;
    }

    public int getnDonationCarrot() {
        return nDonationCarrot;
    }

    public void setnDonationCarrot(int nDonationCarrot) {
        this.nDonationCarrot = nDonationCarrot;
    }

    public String getStrUpdateDate() {
        return strUpdateDate;
    }

    public void setStrUpdateDate(String strUpdateDate) {
        this.strUpdateDate = strUpdateDate;
    }

    public String getStrThumbFile() {
        return strThumbFile;
    }

    public void setStrThumbFile(String strThumbFile) {
        this.strThumbFile = strThumbFile;
    }

    public boolean isbPosterThumbnail() {
        return bPosterThumbnail;
    }

    public void setbPosterThumbnail(boolean bPosterThumbnail) {
        this.bPosterThumbnail = bPosterThumbnail;
    }

    public int getnUserStatus() {
        return nUserStatus;
    }

    public void setnUserStatus(int nUserStatus) {
        this.nUserStatus = nUserStatus;
    }
}
