package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/7/13.
 */

public class BindBankCardBean extends BaseBean{
    private static final long serialVersionUID = 5103764482789651506L;

    /**
     * retcode : 0
     * retmsg : success
     * card_info_id : 1
     * tuid : 44
     * bank_id : 1
     * bank_type : 1
     * bank_card_user : test
     * bank_card_no : 12345678
     * status : 0
     * create_time : 1481873851081
     */

    private String message;
    private int error;
    private int retcode;
    private String retmsg;
    private int card_info_id;
    private int tuid;
    private int bank_id;
    private int bank_type;
    private String bank_card_user;
    private String bank_card_no;
    private int status;
    private long create_time;

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

    public int getCard_info_id() {
        return card_info_id;
    }

    public void setCard_info_id(int card_info_id) {
        this.card_info_id = card_info_id;
    }

    public int getTuid() {
        return tuid;
    }

    public void setTuid(int tuid) {
        this.tuid = tuid;
    }

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getBank_type() {
        return bank_type;
    }

    public void setBank_type(int bank_type) {
        this.bank_type = bank_type;
    }

    public String getBank_card_user() {
        return bank_card_user;
    }

    public void setBank_card_user(String bank_card_user) {
        this.bank_card_user = bank_card_user;
    }

    public String getBank_card_no() {
        return bank_card_no;
    }

    public void setBank_card_no(String bank_card_no) {
        this.bank_card_no = bank_card_no;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
