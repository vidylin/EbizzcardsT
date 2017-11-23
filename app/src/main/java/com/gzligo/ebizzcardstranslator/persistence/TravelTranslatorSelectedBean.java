package com.gzligo.ebizzcardstranslator.persistence;

import java.util.List;

/**
 * Created by Lwd on 2017/9/14.
 */

public class TravelTranslatorSelectedBean extends BaseBean{
    private String sessionId;
    private String fromUserId;
    private String userName;
    private String portrait;
    private List<Integer> languageIds;
    private long selectTime;
    private Integer transType;// 语音：0;视频：1;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public List<Integer> getLanguageIds() {
        return languageIds;
    }

    public void setLanguageIds(List<Integer> languageIds) {
        this.languageIds = languageIds;
    }

    public long getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(long selectTime) {
        this.selectTime = selectTime;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }
}
