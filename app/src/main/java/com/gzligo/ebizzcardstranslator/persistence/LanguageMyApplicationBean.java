package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZuoJian on 2017/8/3.
 */

public class LanguageMyApplicationBean extends BaseBean{

    private int language_id;
    private List<String> cers;

    public List<String> getCers() {
        return cers;
    }

    public void setCers(List<String> cers) {
        this.cers = cers;
    }

    public int getLanguage_id() {

        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }
}
