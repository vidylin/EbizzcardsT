package com.gzligo.ebizzcardstranslator.persistence;

import java.util.List;

/**
 * Created by ZuoJian on 2017/6/24.
 */

public class AddLanguageBean extends BaseBean{
    private int language_id;
    private List<String> cers;

    public Integer getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public List<String> getCers() {
        return cers;
    }

    public void setCers(List<String> cers) {
        this.cers = cers;
    }
}
