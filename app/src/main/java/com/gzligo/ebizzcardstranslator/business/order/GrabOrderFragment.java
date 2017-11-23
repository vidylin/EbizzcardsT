package com.gzligo.ebizzcardstranslator.business.order;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseFragment;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.common.FragmentHeader;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/12.
 */

public class GrabOrderFragment extends BaseFragment {
    private static final int FRAGMENT_ONE = 0;
    private static final int FRAGMENT_TWO = 1;
    @BindView(R.id.fragment_head) FragmentHeader fragmentHeader;
    private FragmentManager fragmentManager;
    private CallOrderFragment callOrderFragment;
    private OrderFragment orderFragment;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grab_order, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        fragmentManager = getChildFragmentManager();
    }

    @Override
    public void initViews() {
        String[] str = new String[]{getResources().getString(R.string.grab_order_dialogue),getResources().getString(R.string.grab_order_call)};
        fragmentHeader.setHeadName(str);
        showFragment(FRAGMENT_ONE);
    }

    @Override
    public void initEvents() {
        fragmentHeader.initEvents(new FragmentHeader.OnShowFragmentListener() {
            @Override
            public void onShowFragmentListener(int which) {
                showFragment(which);
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
    }

    public void showFragment(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragment(ft);
        switch (index) {
            case FRAGMENT_ONE:
                if (orderFragment == null) {
                    orderFragment = new OrderFragment();
                    ft.add(R.id.fragment_content, orderFragment);
                } else {
                    ft.show(orderFragment);
                }
                break;
            case FRAGMENT_TWO:
                if (callOrderFragment == null) {
                    callOrderFragment = new CallOrderFragment();
                    ft.add(R.id.fragment_content, callOrderFragment);
                } else {
                    ft.show(callOrderFragment);
                }
                break;
        }
        ft.commit();
    }

    public void hideFragment(FragmentTransaction ft) {
        if (orderFragment != null) {
            ft.hide(orderFragment);
        }
        if (callOrderFragment != null) {
            ft.hide(callOrderFragment);
        }
    }

    public void requestPermission(){
        callOrderFragment.requestPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentHeader.tabIndicator();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentHeader.destroyUnbinder();
    }
}
