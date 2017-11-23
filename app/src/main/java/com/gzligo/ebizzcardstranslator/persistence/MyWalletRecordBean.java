package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRecordBean<T> extends BaseBean{

    private static final long serialVersionUID = 4186371488310486683L;
    /**
     * retcode : 0
     * retmsg : success
     * list : [{"order_no":"w2016121616470410","tuid":44,"currency_type":0,"amount":113,"card_info_id":6,"bank_order":"","remark":"","status":0,"create_time":1481878024197,"last_modify_time":1481878024197}]
     */

    private int retcode;
    private String retmsg;
    private String order_no;
    private List<T> list;

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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
}
