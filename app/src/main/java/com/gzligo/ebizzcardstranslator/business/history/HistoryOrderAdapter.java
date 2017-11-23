package com.gzligo.ebizzcardstranslator.business.history;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderList;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Lwd on 2017/6/5.
 */

public class HistoryOrderAdapter extends BaseAdapter<TranslatorOrderList> {
    private List<TranslatorOrderList> translatorOrderLists;
    private TreeMap<Integer, LanguagesBean> treeMap;

    public HistoryOrderAdapter(List<TranslatorOrderList> translatorOrderLists, TreeMap<Integer, LanguagesBean> treeMap) {
        super(translatorOrderLists);
        this.translatorOrderLists = translatorOrderLists;
        this.treeMap = treeMap;
    }

    @Override
    public BaseHolder<TranslatorOrderList> getHolder(View v, int viewType) {
        return new HistoryHolder(v, translatorOrderLists, treeMap);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.fragment_history_order_adapter_item;
    }


}
