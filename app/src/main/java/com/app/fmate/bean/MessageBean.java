package com.app.fmate.bean;

public class MessageBean {

    int type;
    String title;
    String content;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MessageBean(int type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
