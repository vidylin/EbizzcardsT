package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lwd on 2017/9/20.
 */

public class TravelTranslateBean implements Serializable{

    /**
     * error : 0
     * message : success
     * data : [{"order_no":"aaaaaaaaaaaa","huser":{"nickname":" サタン法シーザー","uid":"1","portrait":"group1/M00/00/10/CgAAz1glU2SAQT63AAZ4MnDA3jU338.jpg"},"lang_id1":1,"lang_id2":2,"trans_time":10800,"trans_fee":123,"currency":1,"status":3,"remark":"cxvcxz","score":456,"start_time":1476427199000,"end_time":1476427199000}]
     */

    private int error;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * order_no : aaaaaaaaaaaa
         * huser : {"nickname":" サタン法シーザー","uid":"1","portrait":"group1/M00/00/10/CgAAz1glU2SAQT63AAZ4MnDA3jU338.jpg"}
         * lang_id1 : 1
         * lang_id2 : 2
         * trans_time : 10800
         * trans_fee : 123
         * currency : 1
         * status : 3
         * remark : cxvcxz
         * score : 456
         * start_time : 1476427199000
         * end_time : 1476427199000
         */

        private String order_no;
        private HuserBean huser;
        private int lang_id1;
        private int lang_id2;
        private int trans_time;
        private int trans_fee;
        private int currency_type;
        private int status;
        private int type;
        private String remark;
        private int score;
        private long start_time;
        private long end_time;

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public HuserBean getHuser() {
            return huser;
        }

        public void setHuser(HuserBean huser) {
            this.huser = huser;
        }

        public int getLang_id1() {
            return lang_id1;
        }

        public void setLang_id1(int lang_id1) {
            this.lang_id1 = lang_id1;
        }

        public int getLang_id2() {
            return lang_id2;
        }

        public void setLang_id2(int lang_id2) {
            this.lang_id2 = lang_id2;
        }

        public int getTrans_time() {
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

        public int getCurrency_type() {
            return currency_type;
        }

        public void setCurrency_type(int currency_type) {
            this.currency_type = currency_type;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public static class HuserBean {
            /**
             * nickname :  サタン法シーザー
             * uid : 1
             * portrait : group1/M00/00/10/CgAAz1glU2SAQT63AAZ4MnDA3jU338.jpg
             */

            private String nickname;
            private String uid;
            private String portrait;

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getPortrait() {
                return portrait;
            }

            public void setPortrait(String portrait) {
                this.portrait = portrait;
            }
        }
    }
}
