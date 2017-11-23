package com.gzligo.ebizzcardstranslator.persistence;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/6/7.
 */
@Entity
public class TranslatorSelectedBean extends BaseBean{
    private static final long serialVersionUID = 8079758517037490094L;
    @Id
    private Long id;
    private String sessionId;
    private String fromName;
    @NotNull
    private String fromUserId;
    private Integer fromLangId;
    private String fromPortraitId;
    private String toName;
    @NotNull
    private String toUserId;
    private Integer toLangId;
    private String toPortraitId;
    private String translatorMsg;
    private String reason;
    private Long endTime;
    private Integer status = 0;
    private Integer unTransMsg;
    private Boolean isTranslating=false;
    private Long notifyTime;
    private Long updateTime;
    @Generated(hash = 1665123603)
    public TranslatorSelectedBean(Long id, String sessionId, String fromName,
            @NotNull String fromUserId, Integer fromLangId, String fromPortraitId,
            String toName, @NotNull String toUserId, Integer toLangId,
            String toPortraitId, String translatorMsg, String reason, Long endTime,
            Integer status, Integer unTransMsg, Boolean isTranslating,
            Long notifyTime, Long updateTime) {
        this.id = id;
        this.sessionId = sessionId;
        this.fromName = fromName;
        this.fromUserId = fromUserId;
        this.fromLangId = fromLangId;
        this.fromPortraitId = fromPortraitId;
        this.toName = toName;
        this.toUserId = toUserId;
        this.toLangId = toLangId;
        this.toPortraitId = toPortraitId;
        this.translatorMsg = translatorMsg;
        this.reason = reason;
        this.endTime = endTime;
        this.status = status;
        this.unTransMsg = unTransMsg;
        this.isTranslating = isTranslating;
        this.notifyTime = notifyTime;
        this.updateTime = updateTime;
    }
    @Generated(hash = 399255826)
    public TranslatorSelectedBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSessionId() {
        return this.sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public String getFromName() {
        return this.fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    public String getFromUserId() {
        return this.fromUserId;
    }
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    public Integer getFromLangId() {
        return this.fromLangId;
    }
    public void setFromLangId(Integer fromLangId) {
        this.fromLangId = fromLangId;
    }
    public String getFromPortraitId() {
        return this.fromPortraitId;
    }
    public void setFromPortraitId(String fromPortraitId) {
        this.fromPortraitId = fromPortraitId;
    }
    public String getToName() {
        return this.toName;
    }
    public void setToName(String toName) {
        this.toName = toName;
    }
    public String getToUserId() {
        return this.toUserId;
    }
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    public Integer getToLangId() {
        return this.toLangId;
    }
    public void setToLangId(Integer toLangId) {
        this.toLangId = toLangId;
    }
    public String getToPortraitId() {
        return this.toPortraitId;
    }
    public void setToPortraitId(String toPortraitId) {
        this.toPortraitId = toPortraitId;
    }
    public String getTranslatorMsg() {
        return this.translatorMsg;
    }
    public void setTranslatorMsg(String translatorMsg) {
        this.translatorMsg = translatorMsg;
    }
    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Long getEndTime() {
        return this.endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    public Integer getStatus() {
        return this.status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getUnTransMsg() {
        return this.unTransMsg;
    }
    public void setUnTransMsg(Integer unTransMsg) {
        this.unTransMsg = unTransMsg;
    }
    public Boolean getIsTranslating() {
        return this.isTranslating;
    }
    public void setIsTranslating(Boolean isTranslating) {
        this.isTranslating = isTranslating;
    }
    public Long getNotifyTime() {
        return this.notifyTime;
    }
    public void setNotifyTime(Long notifyTime) {
        this.notifyTime = notifyTime;
    }
    public Long getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
   
}
