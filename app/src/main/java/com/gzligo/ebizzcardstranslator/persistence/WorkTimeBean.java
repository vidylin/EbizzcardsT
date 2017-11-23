package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by ZuoJian on 2017/6/19.
 */

public class WorkTimeBean extends BaseBean{

    private String on;
    private String off;

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getOff() {
        return off;
    }

    public void setOff(String off) {
        this.off = off;
    }
}
