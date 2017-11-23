package com.gzligo.ebizzcardstranslator.base.delegate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;

/**
 * Created by xfast on 2017/5/26.
 */

public interface IFragment<P extends IPresenter> {
    Fragment getFragment();

    P getPresenter();

    void setPresenter(P presenter);

    P createPresenter();

    void setIDelegate(IFragmentDelegate delegate);

    IFragmentDelegate getIDelegate();

    View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void initData(Bundle savedInstanceState);

    void initViews();

    void initEvents();
}
