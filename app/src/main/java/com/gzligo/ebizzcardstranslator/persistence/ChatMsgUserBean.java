package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/5/27.
 */

public class ChatMsgUserBean extends BaseBean{

    private String name;
    private String user_id;
    private int lang_id;
    private String lang_str;
    private String portrait_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getLang_id() {
        return lang_id;
    }

    public void setLang_id(int lang_id) {
        this.lang_id = lang_id;
    }

    public String getPortrait_id() {
        return portrait_id;
    }

    public void setPortrait_id(String portrait_id) {
        this.portrait_id = portrait_id;
    }

    public String getLang_str() {
        return lang_str;
    }

    public void setLang_str(String lang_str) {
        this.lang_str = lang_str;
    }
}
