package com.gzligo.ebizzcardstranslator.base.delegate;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xfast on 2017/5/26.
 */

public class FragmentDelegateImpl implements IFragmentDelegate {

    private IFragment mIFragment;
    private Unbinder mUnbinder;


    public FragmentDelegateImpl(IFragment fragment) {
        mIFragment = fragment;
    }

    @Override
    public void onAttach(Context context) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIFragment.setPresenter(mIFragment.createPresenter());
    }

    @Override
    public void onCreateView(View view, Bundle savedInstanceState) {
        if (view != null) {
            mUnbinder = ButterKnife.bind(mIFragment.getFragment(), view);
        }
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        mIFragment.initData(savedInstanceState);
        mIFragment.initViews();
        mIFragment.initEvents();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            try {
                mUnbinder.unbind();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                //fix Bindings already cleared
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mIFragment.getPresenter() != null) {
            mIFragment.getPresenter().onDestroy();
        }
        mUnbinder = null;
        mIFragment = null;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public boolean isAdded() {
        return mIFragment != null && mIFragment.getFragment() != null && mIFragment.getFragment().isAdded();
    }

}
