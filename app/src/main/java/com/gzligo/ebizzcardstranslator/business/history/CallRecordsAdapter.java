package com.gzligo.ebizzcardstranslator.business.history;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslateBean;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Lwd on 2017/9/15.
 */

public class CallRecordsAdapter extends BaseAdapter<TravelTranslateBean.DataBean> {

    private TreeMap<Integer, LanguagesBean> languagesBeanTreeMap;

    public CallRecordsAdapter(List<TravelTranslateBean.DataBean> infos, TreeMap<Integer, LanguagesBean> languagesBeanTreeMap) {
        super(infos);
        this.languagesBeanTreeMap = languagesBeanTreeMap;
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        return new CallRecordsHolder(v, languagesBeanTreeMap);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.call_records_dapater_item;
    }

}
