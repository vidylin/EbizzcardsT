package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by ZuoJian on 2017/6/5.
 */

public class ErrorMessageBean extends BaseBean{

    private int error;
    private String message;

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

}
