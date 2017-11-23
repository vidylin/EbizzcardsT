package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.OnRefreshListener;
import com.gzligo.ebizzcardstranslator.common.refreshlayout.RefreshLayout;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by LWD on 2017/6/12.
 */

public class MyWalletRecordActivity extends BaseActivity<MyWalletRecordPresenter> {

    @BindView(R.id.my_wallet_actionbar) ToolActionBar myWalletActionbar;
    @BindView(R.id.my_wallet_record_recycler) RecyclerView myWalletRecordRecycler;
    @BindView(R.id.history_order_rv_rl) RefreshLayout historyOrderRvRl;
    @BindView(R.id.no_my_wallet_record_order) LinearLayout noMyWalletRecordOrder;

    private static final int ON_REFRESH_DATA = 0x91;
    private static final int ON_LOAD_MORE_DATA = 0x92;
    private static final int GET_MY_WALLET_RECORD_SUCCESS = 0x90;
    private MyWalletRecordAdapter myWalletRecordAdapter;
    private List<MyWalletRecord> list;
    private String until = "0";
    private String since;
    private String count = "20";

    @Override
    public MyWalletRecordPresenter createPresenter() {
        return new MyWalletRecordPresenter(new MyWalletRecordRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_my_wallet_record;
    }

    @Override
    public void initData() {
        if (null == myWalletRecordAdapter) {
            if (null == list) {
                list = new ArrayList<>();
            }
            myWalletRecordAdapter = new MyWalletRecordAdapter(list);
            myWalletRecordRecycler.setAdapter(myWalletRecordAdapter);
        }
        since = System.currentTimeMillis() + "";
        getPresenter().requestGetWithdrawOrder(Message.obtain(this, new String[]{until, since, count}),ON_REFRESH_DATA);
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyWalletRecordActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myWalletRecordRecycler.setLayoutManager(layoutManager);
        historyOrderRvRl.setEnableLoadmore(true);
    }

    @Override
    public void initEvents() {
        historyOrderRvRl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onLoadMore() {
                super.onLoadMore();
                int index = list.size();
                if (index > 0) {
                    MyWalletRecord myWalletRecord = list.get(index - 1);
                    since = myWalletRecord.getLast_modify_time()-1 + "";
                    getPresenter().requestGetWithdrawOrder(Message.obtain(MyWalletRecordActivity.this,
                            new String[]{until, since, count}),ON_LOAD_MORE_DATA);
                }
            }

            @Override
            public void onRefresh() {
                super.onRefresh();
                list.clear();
                since = System.currentTimeMillis() + "";
                getPresenter().requestGetWithdrawOrder(Message.obtain(MyWalletRecordActivity.this, new String[]{until, since, count}),ON_REFRESH_DATA);
            }
        });
        myWalletRecordAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<MyWalletRecord>() {
            @Override
            public void onItemClick(View view, int viewType, MyWalletRecord data, int position) {
                startActivity(new Intent(MyWalletRecordActivity.this, MyWalletRecordDetailActivity.class)
                        .putExtra("MyWalletRecord", data));
            }
        });
    }

    @OnClick({R.id.tv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case GET_MY_WALLET_RECORD_SUCCESS:
                MyWalletRecordBean myWalletRecordBean = (MyWalletRecordBean) message.obj;
                int type = message.arg1;
                if (null != myWalletRecordBean) {
                    List<MyWalletRecord> listBeen = myWalletRecordBean.getList();
                    if (null != listBeen && listBeen.size() > 0) {
                        noMyWalletRecordOrder.setVisibility(View.GONE);
                        historyOrderRvRl.setVisibility(View.VISIBLE);
                        list.addAll(listBeen);
                        myWalletRecordAdapter.notifyDataSetChanged();
                        if(listBeen.size()<20){
                            historyOrderRvRl.setEnableLoadmore(false);
                        }
                    }
                    if(ON_LOAD_MORE_DATA==type){
                        historyOrderRvRl.finishLoadmore();
                    }
                } else {
                    noMyWalletRecordOrder.setVisibility(View.VISIBLE);
                    historyOrderRvRl.setVisibility(View.GONE);
                    if(ON_LOAD_MORE_DATA==type){
                        historyOrderRvRl.finishLoadmore();
                        historyOrderRvRl.setEnableLoadmore(false);
                    }
                }
                if(ON_REFRESH_DATA==type){
                    historyOrderRvRl.finishRefreshing();
                    historyOrderRvRl.setEnableLoadmore(true);
                }
                break;
        }
    }

}
