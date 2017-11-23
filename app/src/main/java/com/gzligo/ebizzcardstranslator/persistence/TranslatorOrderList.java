package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/5/27.
 */

public class TranslatorOrderList extends BaseBean{
    private String order_no;
    private ChatMsgUserBean user1;
    private ChatMsgUserBean user2;
    private long trans_time;
    private int trans_fee;
    private int currency;
    private int status;
    private String remark;
    private int score;
    private long start_time;
    private long end_time;

    public long getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(int trans_time) {
        this.trans_time = trans_time;
    }

    public int getTrans_fee() {
        return trans_fee;
    }

    public void setTrans_fee(int trans_fee) {
        this.trans_fee = trans_fee;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public ChatMsgUserBean getUser1() {
        return user1;
    }

    public void setUser1(ChatMsgUserBean user1) {
        this.user1 = user1;
    }

    public ChatMsgUserBean getUser2() {
        return user2;
    }

    public void setUser2(ChatMsgUserBean user2) {
        this.user2 = user2;
    }

    public void setTrans_time(long trans_time) {
        this.trans_time = trans_time;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
}
