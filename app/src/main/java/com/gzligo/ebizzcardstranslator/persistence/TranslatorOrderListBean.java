package com.gzligo.ebizzcardstranslator.persistence;

import java.util.List;

/**
 * Created by Lwd on 2017/5/27.
 */

public class TranslatorOrderListBean extends BaseBean {
    private int retcode;
    private String retmsg;
    private List<TranslatorOrderList> list;

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

    public List<TranslatorOrderList> getList() {
        return list;
    }

    public void setList(List<TranslatorOrderList> list) {
        this.list = list;
    }
}
