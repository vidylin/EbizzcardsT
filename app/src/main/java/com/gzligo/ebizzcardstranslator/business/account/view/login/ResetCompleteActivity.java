package com.gzligo.ebizzcardstranslator.business.account.view.login;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;

import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/27.
 */

public class ResetCompleteActivity extends BaseActivity{
    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_reset_complete;
    }

    @Override
    public void initViews() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvents() {
    }

    @OnClick(R.id.reset_complete_btn)
    public void onClick(){
        finish();
    }
}
