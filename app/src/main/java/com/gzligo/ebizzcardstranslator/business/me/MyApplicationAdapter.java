package com.gzligo.ebizzcardstranslator.business.me;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationBean;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by ZuoJian on 2017/8/3.
 */

public class MyApplicationAdapter extends BaseAdapter<MyApplicationBean>{

    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private List<MyApplicationBean> mList;

    public MyApplicationAdapter(List<MyApplicationBean> list, TreeMap<Integer,LanguagesBean> beanTreeMap) {
        super(list);
        mList = list;
        this.beanTreeMap = beanTreeMap;
    }

    @Override
    public MyApplicationHolder getHolder(View v, int viewType) {
        return new MyApplicationHolder(v,mList,beanTreeMap);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.my_application_item;
    }

    public void updateMyList(List<MyApplicationBean> list){
        this.mList = list;
    }
}
