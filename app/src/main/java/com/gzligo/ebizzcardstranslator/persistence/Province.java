package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/13.
 */

public class Province extends BaseBean{
    private static final long serialVersionUID = 6413244905259865655L;

    /**
     * prov_id : 1
     * zh_name : 广东
     * en_name : GuangDong
     * state_id : 1
     */

    private int prov_id;
    private String zh_name;
    private String en_name;
    private int state_id;

    public int getProv_id() {
        return prov_id;
    }

    public void setProv_id(int prov_id) {
        this.prov_id = prov_id;
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

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }
}
