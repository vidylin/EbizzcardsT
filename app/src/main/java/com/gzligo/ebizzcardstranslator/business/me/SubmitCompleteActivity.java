package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;

import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/5/31.
 */

public class SubmitCompleteActivity extends BaseActivity<PersonalPresenter> implements IView{

    public static final int AUDIT_START_UP = 0x00;
    private String from;

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter(new PersonalRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_submit_complete;
    }

    @Override
    public void initData() {
        from = getIntent().getStringExtra(TranslatorConstants.Common.FROM);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initEvents() {

    }

    @OnClick(R.id.submit_complete_btn)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_complete_btn:
                if (from != null && from.equals("apply")){
                    finish();
                }else {
                    getPresenter().requestAuditStartup(Message.obtain(this), true);
                }
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case AUDIT_START_UP:
                if (((ErrorMessageBean)message.obj).getError()==0) {
                    if (from != null && from.equals("main")){
                        MainActivity.start(this);
                        finish();
                    }else if (from==null){
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
        }
    }
}
