package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRecord extends BaseBean{
    private static final long serialVersionUID = -7387432991412987353L;
    /**
     * order_no : w2016121616470410
     * tuid : 44
     * currency_type : 0
     * amount : 113
     * card_info_id : 6
     * bank_order :
     * remark :
     * status : 0
     * create_time : 1481878024197
     * last_modify_time : 1481878024197
     */

    private String order_no;
    private int tuid;
    private int currency_type;
    private int amount;
    private int card_info_id;
    private String bank_order;
    private String remark;
    private int status;
    private long create_time;
    private long last_modify_time;

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getTuid() {
        return tuid;
    }

    public void setTuid(int tuid) {
        this.tuid = tuid;
    }

    public int getCurrency_type() {
        return currency_type;
    }

    public void setCurrency_type(int currency_type) {
        this.currency_type = currency_type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCard_info_id() {
        return card_info_id;
    }

    public void setCard_info_id(int card_info_id) {
        this.card_info_id = card_info_id;
    }

    public String getBank_order() {
        return bank_order;
    }

    public void setBank_order(String bank_order) {
        this.bank_order = bank_order;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(long last_modify_time) {
        this.last_modify_time = last_modify_time;
    }
}
