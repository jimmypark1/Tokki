package com.Whowant.Tokki.VO;

public class CarrotVO {
    private long id;                                            // 당근 사용 ID
    private String userId;                                      // 사용/적립한 유져 아이디
    private int carrotPoint;                                    // 사용/적립 포인트
    private String useDate;                                     // 사용/적립 날짜디
    private int type;                                           // 0 : 사용함, 1 : 적립함, 2 : 후원받음
    private String donationFrom;                                // 후원 받은 경우 후원산 유저 아이디
    private String donationName;                                // 후원 받은 경우 후원한 유저 이름
    private long donationWorkId;                                // 후원 받은 / 후원한 작품 아이디
    private int dotaionEpisodeOrder;                             // 작품 순서( 1회차, 2회차 등)
    private String donationWorkTitle;                           // 후원 받은 / 후원한 작품 제목
    private String writerName;                                  // 후원 받은 / 후원한 작품의 작가 이름
    private int    nTotalPoint;                                 // 총 합계

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCarrotPoint() {
        return carrotPoint;
    }

    public void setCarrotPoint(int carrotPoint) {
        this.carrotPoint = carrotPoint;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDonationFrom() {
        return donationFrom;
    }

    public void setDonationFrom(String donationFrom) {
        this.donationFrom = donationFrom;
    }

    public long getDonationWorkId() {
        return donationWorkId;
    }

    public void setDonationWorkId(long donationWorkId) {
        this.donationWorkId = donationWorkId;
    }

    public String getDonationWorkTitle() {
        return donationWorkTitle;
    }

    public void setDonationWorkTitle(String donationWorkTitle) {
        this.donationWorkTitle = donationWorkTitle;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public int getnTotalPoint() {
        return nTotalPoint;
    }

    public void setnTotalPoint(int nTotalPoint) {
        this.nTotalPoint = nTotalPoint;
    }

    public String getDonationName() {
        return donationName;
    }

    public void setDonationName(String donationName) {
        this.donationName = donationName;
    }

    public int getDotaionEpisodeOrder() {
        return dotaionEpisodeOrder;
    }

    public void setDotaionEpisodeOrder(int dotaionEpisodeOrder) {
        this.dotaionEpisodeOrder = dotaionEpisodeOrder;
    }
}
