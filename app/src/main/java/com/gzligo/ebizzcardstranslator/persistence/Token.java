package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/9/14.
 */

public class Token extends BaseBean{

    /**
     * error : 0
     * message : success
     * data : {"voip_token":"bbbbbbbb"}
     */

    private int error;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * voip_token : bbbbbbbb
         */

        private String voip_token;

        public String getVoip_token() {
            return voip_token;
        }

        public void setVoip_token(String voip_token) {
            this.voip_token = voip_token;
        }
    }
}
