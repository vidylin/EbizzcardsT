package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by ZuoJian on 2017/6/21.
 */

public class LanguageSkillBean extends BaseBean{

    private int language_id;
    private int level;
    private int status;
    private int result;

    public int getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
