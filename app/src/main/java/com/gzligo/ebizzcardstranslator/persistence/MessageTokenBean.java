package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/6.
 */

public class MessageTokenBean extends BaseBean {

    /**
     * msg_token1 : <string>
     * msg_token2 : <string>
     */

    private String msg_token1;
    private String msg_token2;

    public String getMsg_token1() {
        return msg_token1;
    }

    public void setMsg_token1(String msg_token1) {
        this.msg_token1 = msg_token1;
    }

    public String getMsg_token2() {
        return msg_token2;
    }

    public void setMsg_token2(String msg_token2) {
        this.msg_token2 = msg_token2;
    }
}
