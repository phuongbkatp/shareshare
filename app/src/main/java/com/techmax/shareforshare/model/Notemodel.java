package com.techmax.shareforshare.model;

/**
 * Created by ABC on 9/3/2017.
 */

public class Notemodel {

    private String title;
    private String body;
    private String type;
    private long time;
    private boolean read;

    public Notemodel() {
    }

    public Notemodel(String title, String body, String type, long time, boolean read) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.time = time;
        this.read=read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
