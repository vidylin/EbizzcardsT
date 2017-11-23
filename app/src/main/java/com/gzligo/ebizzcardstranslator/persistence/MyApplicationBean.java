package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by ZuoJian on 2017/8/3.
 */

public class MyApplicationBean extends BaseBean {

    private long create_time;
    private int audit_type;
    private int result;
    private String conclusion;
    private IdentityBean identity;
    private LanguageMyApplicationBean languageskill;

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getAudit_type() {
        return audit_type;
    }

    public void setAudit_type(int audit_type) {
        this.audit_type = audit_type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public IdentityBean getIdentity() {
        return identity;
    }

    public void setIdentity(IdentityBean identity) {
        this.identity = identity;
    }

    public LanguageMyApplicationBean getLanguageskill() {
        return languageskill;
    }

    public void setLanguageskill(LanguageMyApplicationBean languageskill) {
        this.languageskill = languageskill;
    }
}
