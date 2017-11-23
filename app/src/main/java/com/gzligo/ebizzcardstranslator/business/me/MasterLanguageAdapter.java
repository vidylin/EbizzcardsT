package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Lwd on 2017/7/6.
 */

public class MasterLanguageAdapter extends BaseAdapter<LanguageSkillBean> {
    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private IView iView;
    private static final int CONFIRMING_LANGUAGE = 0x01;
    private static final int PASS_LANGUAGE = 0x02;
    private List<LanguageSkillBean> mList;
    private int confirmingFirstPosition;

    public MasterLanguageAdapter(IView iView,List<LanguageSkillBean> list,TreeMap<Integer,LanguagesBean> beanTreeMap,int confirmingFirstPosition) {
        super(list);
        this.beanTreeMap = beanTreeMap;
        this.iView = iView;
        mList = list;
        this.confirmingFirstPosition = confirmingFirstPosition;
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        switch (viewType){
            case PASS_LANGUAGE:
                return new MasterLanguageHolder(v,beanTreeMap,iView);
            case CONFIRMING_LANGUAGE:
                return new MasterConfirmingLanguageHolder(v,beanTreeMap,confirmingFirstPosition);
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        LanguageSkillBean skillBean = mList.get(position);
        if (skillBean.getResult() == 2) {
            return PASS_LANGUAGE;
        }else {
            return CONFIRMING_LANGUAGE;
        }
    }

    @Override
    public int getLayoutResId(int viewType) {
        switch (viewType){
            case PASS_LANGUAGE:
                return R.layout.master_language_item;
            case CONFIRMING_LANGUAGE:
                return R.layout.master_confirming_language_item;
        }
        return -1;
    }
}
