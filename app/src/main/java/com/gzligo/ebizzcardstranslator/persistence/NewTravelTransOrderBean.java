package com.gzligo.ebizzcardstranslator.persistence;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Lwd on 2017/9/13.
 */
@Entity
public class NewTravelTransOrderBean extends BaseBean{
    @Id
    private Long id;
    @NotNull
    private String session_id;
    private String fromUserId;
    private String userName;
    private String portrait;
    private Integer languageFromId;
    private Integer languageToId;
    private Integer transType;// 语音：0;视频：1;
    private Integer duration;
    private Long startTime;
    private String desc;
    private Long effectTime;
    @Generated(hash = 1296903663)
    public NewTravelTransOrderBean(Long id, @NotNull String session_id,
            String fromUserId, String userName, String portrait,
            Integer languageFromId, Integer languageToId, Integer transType,
            Integer duration, Long startTime, String desc, Long effectTime) {
        this.id = id;
        this.session_id = session_id;
        this.fromUserId = fromUserId;
        this.userName = userName;
        this.portrait = portrait;
        this.languageFromId = languageFromId;
        this.languageToId = languageToId;
        this.transType = transType;
        this.duration = duration;
        this.startTime = startTime;
        this.desc = desc;
        this.effectTime = effectTime;
    }
    @Generated(hash = 387306071)
    public NewTravelTransOrderBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSession_id() {
        return this.session_id;
    }
    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
    public String getFromUserId() {
        return this.fromUserId;
    }
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPortrait() {
        return this.portrait;
    }
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
    public Integer getLanguageFromId() {
        return this.languageFromId;
    }
    public void setLanguageFromId(Integer languageFromId) {
        this.languageFromId = languageFromId;
    }
    public Integer getLanguageToId() {
        return this.languageToId;
    }
    public void setLanguageToId(Integer languageToId) {
        this.languageToId = languageToId;
    }
    public Integer getTransType() {
        return this.transType;
    }
    public void setTransType(Integer transType) {
        this.transType = transType;
    }
    public Integer getDuration() {
        return this.duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Long getStartTime() {
        return this.startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
    public String getDesc() {
        return this.desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Long getEffectTime() {
        return this.effectTime;
    }
    public void setEffectTime(Long effectTime) {
        this.effectTime = effectTime;
    }

}
