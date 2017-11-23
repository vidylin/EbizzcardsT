package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/12.
 */

public class Country extends BaseBean{

    private static final long serialVersionUID = 7066871506489694965L;
    /**
     * state_id : 1
     * zh_name : 中国
     * en_name : China
     * cc_code : 86
     */

    private int state_id;
    private String zh_name;
    private String en_name;
    private String cc_code;

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
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

    public String getCc_code() {
        return cc_code;
    }

    public void setCc_code(String cc_code) {
        this.cc_code = cc_code;
    }
}
