package com.lcodecore.tkrefreshlayout;

/**
 * Created by xfast on 2017/7/21.
 */

public abstract class OnRefreshListener extends RefreshListenerAdapter {
    @Override
    public void onRefresh(TwinklingRefreshLayout refreshLayout) {
        super.onRefresh(refreshLayout);
        onRefresh();
    }

    @Override
    public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
        super.onLoadMore(refreshLayout);
        onLoadMore();
    }

    public void onLoadMore() {

    }

    public abstract void onRefresh();
}
