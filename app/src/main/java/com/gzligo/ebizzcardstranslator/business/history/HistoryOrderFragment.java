package com.gzligo.ebizzcardstranslator.business.history;

import android.content.Context;
import android.content.Intent;
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
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.OnRefreshListener;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.RefreshLayout;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderList;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorOrderListBean;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.R.id.history_order_rv_rl;

/**
 * Created by Lwd on 2017/5/24.
 */

public class HistoryOrderFragment extends BaseFragment<HistoryOrderPresenter> {
    private static final int LOAD_MORE = 0x97;
    private static final int REFRESH = 0x96;
    private HistoryOrderAdapter historyOrderAdapter;
    private String count = "20";
    private Context context;
    private List<TranslatorOrderList> lists;
    private TreeMap<Integer, LanguagesBean> languagesBeanTreeMap;

    @BindView(R.id.no_history_order) LinearLayout noHistoryOrder;
    @BindView(R.id.history_order_rv) RecyclerView historyOrderRv;
    @BindView(history_order_rv_rl) RefreshLayout mTwinklingRefreshLayout;

    @Override
    public HistoryOrderPresenter createPresenter() {
        return new HistoryOrderPresenter(new HistoryOrderRepository(), this);
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_order, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (lists == null) {
            lists = new ArrayList<>();
            languagesBeanTreeMap = new TreeMap<>();
        }
        if (historyOrderAdapter == null) {
            historyOrderAdapter = new HistoryOrderAdapter(lists, languagesBeanTreeMap);
            historyOrderRv.setAdapter(historyOrderAdapter);
        }
        context = getActivity();
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyOrderRv.setLayoutManager(layoutManager);
        mTwinklingRefreshLayout.setRefreshing(true);
    }

    @Override
    public void initEvents() {
        mTwinklingRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onLoadMore() {
                super.onLoadMore();
                int index = lists.size();
                if (index > 0) {
                    TranslatorOrderList orderList = lists.get(index - 1);
                    getPresenter().requestTranslatorOrderList("0",
                            orderList.getStart_time()-1 + "", count, true, LOAD_MORE);
                }
            }

            @Override
            public void onRefresh() {
                super.onRefresh();
                lists.clear();
                String nowTime = System.currentTimeMillis() + "";
                getPresenter().requestTranslatorOrderList("0", nowTime, count, true, REFRESH);
            }
        });

        historyOrderAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<TranslatorOrderList>() {
            @Override
            public void onItemClick(View view, int viewType, TranslatorOrderList data, int position) {
                Intent intent = new Intent(getActivity(), HistoryOrderDetailActivity.class);
                intent.putExtra("TRANSLATE_HISTORY_DATA", lists.get(position));
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case LOAD_MORE:
                TranslatorOrderListBean t = (TranslatorOrderListBean) message.obj;
                hasHistoryData(t);
                mTwinklingRefreshLayout.finishLoadmore();
                break;
            case REFRESH:
                t = (TranslatorOrderListBean) message.obj;
                mTwinklingRefreshLayout.finishRefreshing();
                mTwinklingRefreshLayout.setEnableLoadmore(true);
                hasHistoryData(t);
                break;
        }
    }

    private void hasHistoryData(TranslatorOrderListBean t) {
        if (null != t.getList() && t.getList().size() > 0) {
            int number = t.getList().size();
            lists.addAll(t.getList());
            noHistoryOrder.setVisibility(View.GONE);
            mTwinklingRefreshLayout.setVisibility(View.VISIBLE);
            TreeMap<Integer, LanguagesBean> treeMap = getPresenter().getTreeMap();
            if (null != treeMap && treeMap.size() > 0) {
                languagesBeanTreeMap.putAll(treeMap);
            }
            historyOrderAdapter.notifyDataSetChanged();
            if(number<20){
                mTwinklingRefreshLayout.setEnableLoadmore(false);
            }
        }
    }

}

