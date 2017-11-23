package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/13.
 */

public class BankPoint extends BaseBean{
    private static final long serialVersionUID = -6951115197143879307L;

    /**
     * bank_id : 1
     * bank_type : 1
     * cnaps : aaa
     * swift : bbb
     * zh_name : aaa
     * en_name : aaaaa
     * state_id : 1
     * prov_id : 1
     * city_id : 1
     * addr : eee
     */

    private int bank_id;
    private int bank_type;
    private String cnaps;
    private String swift;
    private String zh_name;
    private String en_name;
    private int state_id;
    private int prov_id;
    private int city_id;
    private String addr;

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getBank_type() {
        return bank_type;
    }

    public void setBank_type(int bank_type) {
        this.bank_type = bank_type;
    }

    public String getCnaps() {
        return cnaps;
    }

    public void setCnaps(String cnaps) {
        this.cnaps = cnaps;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
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

    public int getProv_id() {
        return prov_id;
    }

    public void setProv_id(int prov_id) {
        this.prov_id = prov_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
