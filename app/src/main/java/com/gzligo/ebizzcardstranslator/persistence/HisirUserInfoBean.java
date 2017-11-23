package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/9/11.
 */

public class HisirUserInfoBean {

    /**
     * error : 0
     * message : success
     * data : {"uid":2,"nickname":"Fanming Android","portrait":"group1/M00/01/7F/CgAAC1kKjqmAbBwOAAEF1ydXJDE050.png"}
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
         * uid : 2
         * nickname : Fanming Android
         * portrait : group1/M00/01/7F/CgAAC1kKjqmAbBwOAAEF1ydXJDE050.png
         */

        private int uid;
        private String nickname;
        private String portrait;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }
    }
}
