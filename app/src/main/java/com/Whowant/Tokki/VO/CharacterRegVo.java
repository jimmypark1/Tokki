package com.Whowant.Tokki.VO;

public class CharacterRegVo {
    String name;                    // 이름
    String personality;             // 성격
    String role;                        // 역할
    String imgUri;                  // 이미지
    int location;                   // 등장인물 위치
    int color;                          // 등장인물 컬러
    int txtColorBox;                   // 말풍선 텍스트 컬러 박스
    String colorBg;               // 말풍선 컬러

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTxtColorBox() {
        return txtColorBox;
    }

    public void setTxtColorBox(int txtColorBox) {
        this.txtColorBox = txtColorBox;
    }

    public String getColorBg() {
        return colorBg;
    }

    public void setColorBg(String colorBg) {
        this.colorBg = colorBg;
    }
}
