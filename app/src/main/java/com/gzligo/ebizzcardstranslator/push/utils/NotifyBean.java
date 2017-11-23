package com.gzligo.ebizzcardstranslator.push.utils;

/**
 * Created by Lwd on 2017/9/12.
 */

public class NotifyBean {
    /**
     * clt_time : undefined
     * content : {"body":"u"}
     * from : 1164
     * from_name : 林
     * msg_id : 7e174299-839d-482c-87d3-dde4a6905d97
     * session_id : 82330407-f9ba-46e8-a0f1-a738e62d4ffd
     * time : 1505217218
     * to : 1161
     * translator : T71
     * type : TEXT
     */

    private String clt_time;
    private String content;
    private String from;
    private String from_name;
    private String msg_id;
    private String session_id;
    private int time;
    private String to;
    private String translator;
    private String type;


    public String getClt_time() {
        return clt_time;
    }

    public void setClt_time(String clt_time) {
        this.clt_time = clt_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public class ContentBean{
        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}
