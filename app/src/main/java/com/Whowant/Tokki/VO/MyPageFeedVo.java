package com.Whowant.Tokki.VO;

import java.util.ArrayList;

public class MyPageFeedVo {
    public int type;
    public WorkVO workVO;
    public ArrayList<WriterVO> follow = new ArrayList<>();
    public String noti;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public WorkVO getWorkVO() {
        return workVO;
    }

    public void setWorkVO(WorkVO workVO) {
        this.workVO = workVO;
    }

    public ArrayList<WriterVO> getFollow() {
        return follow;
    }

    public void setFollow(ArrayList<WriterVO> follow) {
        this.follow = follow;
    }

    public String getNoti() {
        return noti;
    }

    public void setNoti(String noti) {
        this.noti = noti;
    }
}
