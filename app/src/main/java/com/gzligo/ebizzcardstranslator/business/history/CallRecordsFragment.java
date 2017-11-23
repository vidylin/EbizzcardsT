package com.gzligo.ebizzcardstranslator.business.history;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseFragment;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.OnRefreshListener;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.RefreshLayout;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslateBean;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/12.
 */

public class CallRecordsFragment extends BaseFragment<CallRecordsPresenter> {

    @BindView(R.id.no_history_order) LinearLayout noHistoryOrder;
    @BindView(R.id.call_order_rv) RecyclerView callOrderRv;
    @BindView(R.id.tr_layout) RefreshLayout trLayout;
    private static final int LOAD_MORE = 0x67;
    private static final int REFRESH = 0x66;
    private String count = "20";
    private List<TravelTranslateBean.DataBean> dataBeanList;
    private CallRecordsAdapter callRecordsAdapter;
    private TreeMap<Integer, LanguagesBean> languagesBeanTreeMap;

    @Override
    public CallRecordsPresenter createPresenter() {
        return new CallRecordsPresenter(new CallRecordsRepository(),this);
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_records, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        dataBeanList = new ArrayList<>();
        languagesBeanTreeMap = new TreeMap<>();
        callRecordsAdapter = new CallRecordsAdapter(dataBeanList,languagesBeanTreeMap);
        callOrderRv.setAdapter(callRecordsAdapter);
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        callOrderRv.setLayoutManager(layoutManager);
        noHistoryOrder.setVisibility(View.GONE);
        trLayout.setVisibility(View.VISIBLE);
        trLayout.setRefreshing(true);
    }

    @Override
    public void initEvents() {
        trLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onLoadMore() {
                super.onLoadMore();
                int index = dataBeanList.size();
                if (index > 0) {
                    TravelTranslateBean.DataBean orderList = dataBeanList.get(index - 1);
                    getPresenter().requestTranslatorTravelOrderList("0",
                            orderList.getStart_time()-1 + "", count, LOAD_MORE);
                }
            }

            @Override
            public void onRefresh() {
                super.onRefresh();
                dataBeanList.clear();
                String nowTime = System.currentTimeMillis() + "";
                getPresenter().requestTranslatorTravelOrderList("0", nowTime, count, REFRESH);
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case LOAD_MORE:
                TravelTranslateBean t = (TravelTranslateBean) message.obj;
                hasHistoryData(t);
                trLayout.finishLoadmore();
                break;
            case REFRESH:
                t = (TravelTranslateBean) message.obj;
                hasHistoryData(t);
                trLayout.finishRefreshing();
                trLayout.setEnableLoadmore(true);
                break;
        }
    }

    private void hasHistoryData(TravelTranslateBean t) {
        if (null != t.getData() && t.getData().size() > 0) {
            int number = t.getData().size();
            dataBeanList.addAll(t.getData());
            TreeMap<Integer, LanguagesBean> treeMap = getPresenter().getTreeMap();
            if (null != treeMap && treeMap.size() > 0) {
                languagesBeanTreeMap.putAll(treeMap);
            }
            callRecordsAdapter.notifyDataSetChanged();
            if(number<20){
                trLayout.setEnableLoadmore(false);
            }
        }else{
            trLayout.setEnableLoadmore(false);
        }
        if(null !=dataBeanList&&dataBeanList.size()==0){
            noHistoryOrder.setVisibility(View.VISIBLE);
            trLayout.setVisibility(View.GONE);
        }
    }
}
