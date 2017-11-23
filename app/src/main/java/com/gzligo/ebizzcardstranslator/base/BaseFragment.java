package com.gzligo.ebizzcardstranslator.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzligo.ebizzcardstranslator.base.delegate.IFragment;
import com.gzligo.ebizzcardstranslator.base.delegate.IFragmentDelegate;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;

/**
 * Created by xfast on 2017/5/22.
 */

public abstract class BaseFragment<P extends IPresenter> extends Fragment implements IFragment<P>, IView {
    private IFragmentDelegate mDelegate;
    private P mPresenter;

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public IFragmentDelegate getIDelegate() {
        return mDelegate;
    }

    @Override
    public void setIDelegate(IFragmentDelegate delegate) {
        mDelegate = delegate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onLayoutView(inflater, container, savedInstanceState);
    }

}
