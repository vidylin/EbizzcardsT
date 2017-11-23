package com.gzligo.ebizzcardstranslator.base.delegate;

import android.os.Bundle;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.utils.Preconditions;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xfast on 2017/5/25.
 */

public class ActivityDelegateImpl implements IActivityDelegate {

    private IActivity mIActivity;
    private Unbinder mUnbinder;


    public ActivityDelegateImpl(IActivity activity) {
        mIActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Preconditions.checkNotNull(mIActivity, "you must set IActivity to be not NULL!");

        mIActivity.setPresenter(mIActivity.createPresenter());

        final int layoutResId = mIActivity.onLayoutResId();
        if (layoutResId > 0) {
            mIActivity.getActivity().setContentView(layoutResId);
        }

        mUnbinder = ButterKnife.bind(mIActivity.getActivity());

        mIActivity.initData();
        mIActivity.initViews();
        mIActivity.initEvents();

        if (mIActivity.isSupportSwipeBack()) {
            mIActivity.getActivity().overridePendingTransition(R.anim.activity_slide_right_in,R.anim.activity_slide_left_out);
        }
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
    public void onDestroy() {
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            try {
                mUnbinder.unbind();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                //fix Bindings already cleared
            }
        }

        if (mIActivity != null && mIActivity.getPresenter() != null) {
            mIActivity.getPresenter().onDestroy();
        }

        mUnbinder = null;
        mIActivity = null;
    }

    @Override
    public void onFinish() {
        if (mIActivity != null && mIActivity.isSupportSwipeBack() && AppManager.get().getActivities().size() > 1) {
            mIActivity.getActivity().overridePendingTransition(R.anim.activity_slide_left_in,R.anim.activity_slide_right_out);
        }
    }
}
