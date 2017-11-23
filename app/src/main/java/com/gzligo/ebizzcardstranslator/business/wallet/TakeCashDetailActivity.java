package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;

import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/14.
 */

public class TakeCashDetailActivity extends BaseActivity implements IView {
    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_take_cash_detail;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {

    }

    @OnClick(R.id.take_detail_btn)
    public void onClick() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
