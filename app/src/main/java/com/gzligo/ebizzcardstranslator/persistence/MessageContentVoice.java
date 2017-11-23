package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/9.
 */

public class MessageContentVoice extends BaseBean {
    private String duration;
    private String url;
    private String fileName;
    private boolean isClicked;
    private String privateToNickname;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
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

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    @Override
    public String toString() {
        return "MessageContentVoice{" +
                "duration=" + duration +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isClicked=" + isClicked +
                ", privateToNickname='" + privateToNickname + '\'' +
                '}';
    }
}
