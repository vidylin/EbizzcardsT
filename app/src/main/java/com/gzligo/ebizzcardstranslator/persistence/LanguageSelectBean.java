package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by ZuoJian on 2017/5/31.
 */

public class LanguageSelectBean extends BaseBean{

    private String name;
    private String select;
    private String language_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }
}
