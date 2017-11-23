package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;

import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/2.
 */

public class MeMsgActivity extends BaseActivity implements IView{
    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_me_msg;
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

    @OnClick({R.id.tv_close})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
        }
    }
}
