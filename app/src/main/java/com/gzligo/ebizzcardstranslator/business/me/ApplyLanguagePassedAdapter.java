package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by ZuoJian on 2017/7/14.
 */

public class ApplyLanguagePassedAdapter extends BaseAdapter<LanguageSkillBean> {

    private static final int PASS_LANGUAGE = 0x02;
    private static final int UNCONFIRMED_LANGUAGE = 0x03;
    private List<LanguageSkillBean> mList;
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private int titlePosition;

    public ApplyLanguagePassedAdapter(List<LanguageSkillBean> list,TreeMap<Integer,LanguagesBean> beanTreeMap,int titlePosition) {
        super(list);
        mList = list;
        this.beanTreeMap = beanTreeMap;
        this.titlePosition = titlePosition;
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        switch (viewType){
            case PASS_LANGUAGE:
                return new ApplyLanguagePassedHolder(v,beanTreeMap);
            case UNCONFIRMED_LANGUAGE:
                return new ApplyLanguageUnconfirmedHolder(v,beanTreeMap,titlePosition);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        LanguageSkillBean selectBean = mList.get(position);
        if (selectBean.getResult()==2){
            return PASS_LANGUAGE;
        }else {
            return UNCONFIRMED_LANGUAGE;
        }
    }

    @Override
    public int getLayoutResId(int viewType) {
        switch (viewType){
            case PASS_LANGUAGE:
                return R.layout.apply_language_passed_item;
            case UNCONFIRMED_LANGUAGE:
                return R.layout.apply_language_unconfirmed_item;
        }
        return -1;
    }
}
