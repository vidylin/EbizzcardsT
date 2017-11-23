package com.gzligo.ebizzcardstranslator.persistence;

import java.util.List;

/**
 * Created by Lwd on 2017/7/18.
 */

public class TranslatorList extends BaseBean {
    private List<TranslatorSelectedBean> translatorSelectedBeen;
    private int position;

    public List<TranslatorSelectedBean> getTranslatorSelectedBeen() {
        return translatorSelectedBeen;
    }

    public void setTranslatorSelectedBeen(List<TranslatorSelectedBean> translatorSelectedBeen) {
        this.translatorSelectedBeen = translatorSelectedBeen;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
