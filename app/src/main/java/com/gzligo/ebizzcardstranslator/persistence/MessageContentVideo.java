package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/9.
 */

public class MessageContentVideo extends BaseBean {
    private double duration;
    private String length;
    private String url;
    private String fileName;
    private String privateToNickname;
    private String description;
    private int width;
    private int height;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrivateToNickname() {
        return privateToNickname;
    }

    public void setPrivateToNickname(String privateToNickname) {
        this.privateToNickname = privateToNickname;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
