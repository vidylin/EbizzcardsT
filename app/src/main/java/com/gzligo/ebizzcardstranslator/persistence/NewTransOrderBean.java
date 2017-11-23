package com.gzligo.ebizzcardstranslator.persistence;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/6/8.
 */
@Entity
public class NewTransOrderBean extends BaseBean{
    private static final long serialVersionUID = -1056677364146835268L;
    @Id
    private Long id;
    @NotNull
    private String orderId;
    private String desc;
    private Integer duration;
    @NotNull
    private Long start;
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
    @NotNull
    private Long effectiveTime;
    @Generated(hash = 393053587)
    public NewTransOrderBean(Long id, @NotNull String orderId, String desc,
            Integer duration, @NotNull Long start, String fromName,
            @NotNull String fromUserId, Integer fromLangId, String fromPortraitId,
            String toName, @NotNull String toUserId, Integer toLangId,
            String toPortraitId, @NotNull Long effectiveTime) {
        this.id = id;
        this.orderId = orderId;
        this.desc = desc;
        this.duration = duration;
        this.start = start;
        this.fromName = fromName;
        this.fromUserId = fromUserId;
        this.fromLangId = fromLangId;
        this.fromPortraitId = fromPortraitId;
        this.toName = toName;
        this.toUserId = toUserId;
        this.toLangId = toLangId;
        this.toPortraitId = toPortraitId;
        this.effectiveTime = effectiveTime;
    }
    @Generated(hash = 1260242418)
    public NewTransOrderBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOrderId() {
        return this.orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getDesc() {
        return this.desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Integer getDuration() {
        return this.duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Long getStart() {
        return this.start;
    }
    public void setStart(Long start) {
        this.start = start;
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
    public Long getEffectiveTime() {
        return this.effectiveTime;
    }
    public void setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
    
}
