package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/5.
 */

public class LanguagesBean extends BaseBean{
    private static final long serialVersionUID = -4031243242527464792L;

    /**
     * language_id : 1
     * name : zh
     * zh_name : 中文
     * en_name : chinese
     * status : 1
     */

    private int language_id;
    private String name;
    private String zh_name;
    private String en_name;
    private int status;

    public int getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZh_name() {
        return zh_name;
    }

    public void setZh_name(String zh_name) {
        this.zh_name = zh_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
