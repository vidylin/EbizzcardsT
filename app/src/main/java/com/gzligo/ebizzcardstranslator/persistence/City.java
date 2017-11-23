package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/13.
 */

public class City extends BaseBean{
    private static final long serialVersionUID = -5792500296038305861L;

    /**
     * city_id : 1
     * zh_name : 广州
     * en_name : GuangZhou
     */

    private int city_id;
    private String zh_name;
    private String en_name;

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
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
}
