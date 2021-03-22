package com.Whowant.Tokki.VO;

import java.util.ArrayList;

public class MainCardVO {               // 메인화면 리스트의 카드 한줄 VO
    private String strHeaderTitle;
    private ArrayList<WorkVO> allItemInCard;
    private int viewType;
    private ArrayList<EventVO> events;

    public MainCardVO() {

    }

    public void setEvents(ArrayList<EventVO> events) {
        this.events = events;
    }

    public ArrayList<EventVO> getEvents() {
        return events;
    }

    public MainCardVO(String strHeaderTitle, ArrayList<WorkVO> allItemInCard) {
        this.strHeaderTitle = strHeaderTitle;
        this.allItemInCard = allItemInCard;
    }

    public String getStrHeaderTitle() {
        return strHeaderTitle;
    }

    public void setStrHeaderTitle(String strHeaderTitle) {
        this.strHeaderTitle = strHeaderTitle;
    }

    public ArrayList<WorkVO> getAllItemInCard() {
        return allItemInCard;
    }

    public void setAllItemInCard(ArrayList<WorkVO> allItemInCard) {
        this.allItemInCard = allItemInCard;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
