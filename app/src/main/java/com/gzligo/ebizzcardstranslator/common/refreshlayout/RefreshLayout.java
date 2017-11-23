package com.gzligo.ebizzcardstranslator.common.refreshlayout;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.header.JuhuaHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

/**
 * Created by Lwd on 2017/11/2.
 */

public class RefreshLayout  extends SmartRefreshLayout {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
//            @NonNull
//            @Override
//            public RefreshHeader createRefreshHeader(Context context, com.scwang.smartrefresh.layout.api.RefreshLayout layout) {
////                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
//                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
//            }
//        });
//
//        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
//            @NonNull
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, com.scwang.smartrefresh.layout.api.RefreshLayout layout) {
////                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
//                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
//            }
//        });
    }


    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        if (!isEnablePureScrollMode()) {
//            if (isEnableRefresh()) {
            setRefreshHeader(new ClassicsHeader(getContext()).setSpinnerStyle(SpinnerStyle.Translate));
//            }
//            if (isEnableLoadmore()) {
            setRefreshFooter(new ClassicsFooter(getContext()).setSpinnerStyle(SpinnerStyle.Translate));
//            }
        }

        hook();

        setReboundDuration(400);
    }

    /**
     * 适配v4.SwipeRefreshLayout
     *
     * @param refreshing refresh
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            autoRefresh();
        } else {
            finishRefreshing();
        }
    }

    /**
     * @param loading load more
     */
    public void setLoading(boolean loading) {
        if (loading) {
            autoLoadmore();
        } else {
            finishLoading();
        }
    }

    @Override
    public SmartRefreshLayout setEnableRefresh(boolean enable) {
        if (enable) {
            resetRefreshNoMoreData();
        } else {
            finishRefreshWithNoMoreData();
        }
        return this;
    }

    @Override
    public SmartRefreshLayout setEnableLoadmore(boolean enable) {
        if (enable) {
            resetLoadNoMoreData();
        } else {
            finishLoadmoreWithNoMoreData();
        }
        return this;
    }


    public void finishRefreshing() {
        finishRefresh(0);
    }

    public void finishLoading() {
        finishLoadmore(0);
    }

    public View getTargetView() {
        return mRefreshContent.getView();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        setOnRefreshLoadmoreListener(listener.getDelegate());
    }

//    public void useEmptyFooter() {
//        setEnableLoadmore(false);
//        setRefreshFooter(new FalsifyFooter(getContext()));
//    }
//
//    public void useEmptyHeader() {
//        setEnableRefresh(false);
//        setRefreshHeader(new FalsifyHeader(getContext()));
//    }

    private void hook() {
        setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            private int mLastLoadmoreOffset;
            private int mLastRefreshOffset;

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {
                if (header instanceof JuhuaHeader) {
                    smoothMove(percent, offset);
                }
            }

            @Override
            public void onFooterReleasing(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {
                smoothMove(percent, offset);
            }

            private void smoothMove(float percent, int offset) {
                if (!(getTargetView() instanceof RecyclerView)) {
                    return;
                }
                if (percent == 1) {
                    mLastRefreshOffset = offset;
                    mLastLoadmoreOffset = offset;
                    return;
                }
                if (percent < 1 && percent >= 0) {
                    if (getState() == RefreshState.RefreshFinish) {
                        int yDelta = mLastRefreshOffset - offset;
                        getTargetView().scrollBy(0, -yDelta);
                        mLastRefreshOffset = offset;
                    }
                    if (getState() == RefreshState.LoadFinish) {
                        int yDelta = mLastLoadmoreOffset - offset;
                        getTargetView().scrollBy(0, yDelta);
                        mLastLoadmoreOffset = offset;
                    }
                }
            }
        });
    }

}
