package com.gzligo.ebizzcardstranslator.persistence;

import java.util.List;

/**
 * Created by Lwd on 2017/6/5.
 */

public class HttpResultBean<T> extends BaseBean{


    /**
     * error : 0
     * message : success
     * obj_id : group1/M00/01/3E/CgAAz1lDc3SAaDKJAAokhgP-CHQ938.amr
     */

    private int error;
    private String message;
    private String token;
    private List<T> data;
    private String obj_id;
    private String order_no;
    private String error_description;

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

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
