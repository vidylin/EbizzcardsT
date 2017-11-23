package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSelectBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by ZuoJian on 2017/7/14.
 */

public class ApplyLanguageUnconfirmedAdapter extends BaseAdapter<LanguageSkillBean>{
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private int titlePosition;

    public ApplyLanguageUnconfirmedAdapter(List<LanguageSkillBean> list,TreeMap<Integer,LanguagesBean> beanTreeMap,int titlePosition) {
        super(list);
        this.beanTreeMap = beanTreeMap;
        this.titlePosition = titlePosition;
    }

    @Override
    public BaseHolder<LanguageSkillBean> getHolder(View v, int viewType) {
        return new ApplyLanguageUnconfirmedHolder(v,beanTreeMap,titlePosition);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.apply_language_unconfirmed_item;
    }

}
