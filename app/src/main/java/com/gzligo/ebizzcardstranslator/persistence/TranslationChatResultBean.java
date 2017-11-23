package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/10.
 */

public class TranslationChatResultBean extends BaseBean {
    private int resultStatus;
    private String msgId;

    public int getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
