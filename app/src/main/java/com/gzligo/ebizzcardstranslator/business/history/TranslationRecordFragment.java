package com.gzligo.ebizzcardstranslator.business.history;

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

public class TranslationRecordFragment extends BaseFragment{
    private static final int FRAGMENT_ONE = 0;
    private static final int FRAGMENT_TWO = 1;
    @BindView(R.id.fragment_head) FragmentHeader fragmentHeader;
    private FragmentManager fragmentManager;
    private CallRecordsFragment callRecordsFragment;
    private HistoryOrderFragment historyOrderFragment;

    @Override
    public void handlePresenterCallback(Message message) {
    }

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
        String[] str = new String[]{getResources().getString(R.string.translation_record_dialogue),getResources().getString(R.string.translation_record_call)};
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

    public void showFragment(int index) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragment(ft);
        switch (index) {
            case FRAGMENT_ONE:
                if (historyOrderFragment == null) {
                    historyOrderFragment = new HistoryOrderFragment();
                    ft.add(R.id.fragment_content, historyOrderFragment);
                } else {
                    ft.show(historyOrderFragment);
                }
                break;
            case FRAGMENT_TWO:
                if (callRecordsFragment == null) {
                    callRecordsFragment = new CallRecordsFragment();
                    ft.add(R.id.fragment_content, callRecordsFragment);
                } else {
                    ft.show(callRecordsFragment);
                }
                break;
        }
        ft.commit();
    }

    public void hideFragment(FragmentTransaction ft) {
        if (historyOrderFragment != null) {
            ft.hide(historyOrderFragment);
        }
        if (callRecordsFragment != null) {
            ft.hide(callRecordsFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!= fragmentHeader){
            fragmentHeader.tabIndicator();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!= fragmentHeader){
            fragmentHeader.destroyUnbinder();
        }
    }
}
