package com.gzligo.ebizzcardstranslator.base.delegate;

import android.app.Activity;

import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;

/**
 * Created by xfast on 2017/5/25.
 */

public interface IActivity<P extends IPresenter> {
    Activity getActivity();

    P getPresenter();

    void setPresenter(P presenter);

    P createPresenter();

    void setIDelegate(IActivityDelegate delegate);

    IActivityDelegate getIDelegate();

    int onLayoutResId();

    void initData();

    void initViews();

    void initEvents();

    boolean isSupportSwipeBack();

}
