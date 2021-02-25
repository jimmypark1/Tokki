package com.Whowant.Tokki.VO;

public class WebWorkVO {

    private String cover;
    private String raw;
    private String content;
    private String title;

    public void setContent(String content) {
        this.content = content;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getCover() {
        return cover;
    }

    public String getRaw() {
        return raw;
    }

    public String getTitle() {
        return title;
    }
}
