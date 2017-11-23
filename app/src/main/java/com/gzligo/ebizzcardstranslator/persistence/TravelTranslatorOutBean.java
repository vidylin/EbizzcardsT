package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/9/14.
 */

public class TravelTranslatorOutBean {
    private String sessionId;
    private int reason;//0:翻译主动退出;1:多久时间没消息;2:客户主动结束;3:没有余额;4:异常情况
    private int duration;
    private long outTime;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }
}
