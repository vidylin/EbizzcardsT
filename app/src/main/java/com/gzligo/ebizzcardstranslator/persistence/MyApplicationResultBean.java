package com.gzligo.ebizzcardstranslator.persistence;

import java.util.List;

/**
 * Created by ZuoJian on 2017/8/3.
 */

public class MyApplicationResultBean extends BaseBean{

    private int error;
    private String message;
    private List<MyApplicationBean> audits;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MyApplicationBean> getAudits() {
        return audits;
    }

    public void setAudits(List<MyApplicationBean> audits) {
        this.audits = audits;
    }
}
