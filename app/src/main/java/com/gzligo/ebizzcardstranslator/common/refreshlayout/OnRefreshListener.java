package com.gzligo.ebizzcardstranslator.common.refreshlayout;

import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

/**
 * Created by xfast on 2017/10/17.
 */

public abstract class OnRefreshListener {

    private OnRefreshLoadmoreListener delegate = new OnRefreshLoadmoreListener() {

        @Override
        public void onRefresh(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {
            OnRefreshListener.this.onRefresh();
        }


        @Override
        public void onLoadmore(com.scwang.smartrefresh.layout.api.RefreshLayout refreshLayout) {
            OnRefreshListener.this.onLoadMore();
        }
    };

    public OnRefreshListener() {

    }


    public void onLoadMore() {

    }

    public void onRefresh() {

    }

    public OnRefreshLoadmoreListener getDelegate() {
        return delegate;
    }
}
