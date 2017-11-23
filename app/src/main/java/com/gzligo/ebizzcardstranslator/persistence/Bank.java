package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/12.
 */

public class Bank extends BaseBean{

    private static final long serialVersionUID = 1312009946680801204L;
    /**
     * type_id : 1
     * zh_name : 中国银行
     * en_name : China bank
     * portrait : abc
     */

    private int type_id;
    private String zh_name;
    private String en_name;
    private String portrait;

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
