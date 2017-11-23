package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/7.
 */

public class MessageContentText extends BaseBean {
    private String body;
    private String privateToNickname;
    private String strangerPhone;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPrivateToNickname() {
        return privateToNickname;
    }

    public void setPrivateToNickname(String privateToNickname) {
        this.privateToNickname = privateToNickname;
    }

    public String getStrangerPhone() {
        return strangerPhone;
    }

    public void setStrangerPhone(String strangerPhone) {
        this.strangerPhone = strangerPhone;
    }
}
