package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZuoJian on 2017/6/20.
 */

public class UserBean extends BaseBean{

    private int error;
    private String message;
    private String tid;
    private String nickname;
    private String username;
    private String cc_code;
    private String phone;
    private long birthday;
    private int identity_status;
    private int sex;
    private float level;
    private String portrait_id;
    private int max_translation_number;
    private List<WorkTimeBean> work_times;
    private List<LanguageSkillBean> languageskills;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCc_code() {
        return cc_code;
    }

    public void setCc_code(String cc_code) {
        this.cc_code = cc_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getIdentity_status() {
        return identity_status;
    }

    public void setIdentity_status(int identity_status) {
        this.identity_status = identity_status;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public String getPortrait_id() {
        return portrait_id;
    }

    public void setPortrait_id(String portrait_id) {
        this.portrait_id = portrait_id;
    }

    public int getMax_translation_number() {
        return max_translation_number;
    }

    public void setMax_translation_number(int max_translation_number) {
        this.max_translation_number = max_translation_number;
    }

    public List<WorkTimeBean> getWork_times() {
        return work_times;
    }

    public void setWork_times(List<WorkTimeBean> work_times) {
        this.work_times = work_times;
    }

    public List<LanguageSkillBean> getLanguageskills() {
        return languageskills;
    }

    public void setLanguageskills(List<LanguageSkillBean> languageskills) {
        this.languageskills = languageskills;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", tid='" + tid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", cc_code='" + cc_code + '\'' +
                ", phone='" + phone + '\'' +
                ", birthday=" + birthday +
                ", identity_status=" + identity_status +
                ", sex=" + sex +
                ", level=" + level +
                ", portrait_id='" + portrait_id + '\'' +
                ", max_translation_number=" + max_translation_number +
                ", work_times=" + work_times +
                ", languageskills=" + languageskills +
                '}';
    }
}