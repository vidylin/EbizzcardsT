package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/7/12.
 */

public class AccountBean extends BaseBean{

    /**
     * retcode : 0
     * retmsg : success
     * tuid : 44
     * currency_type : 0
     * total_amount : 123456
     * frozen_amount : 347
     * has_wpasswd : 0
     * create_time : 1481860447979
     * last_modify_time : 1481860447979
     */

    private int retcode;
    private String retmsg;
    private int tuid;
    private int currency_type;
    private int total_amount;
    private int frozen_amount;
    private int has_wpasswd;
    private long create_time;
    private long last_modify_time;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
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

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public int getFrozen_amount() {
        return frozen_amount;
    }

    public void setFrozen_amount(int frozen_amount) {
        this.frozen_amount = frozen_amount;
    }

    public int getHas_wpasswd() {
        return has_wpasswd;
    }

    public void setHas_wpasswd(int has_wpasswd) {
        this.has_wpasswd = has_wpasswd;
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
