package com.gzligo.ebizzcardstranslator.business.splash;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginPresenter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginRepository;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.persistence.LoginBean;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import static com.gzligo.ebizzcardstranslator.business.account.view.login.LoginActivity.MSG_LOGIN;

/**
 * Created by Lwd on 2017/7/7.
 */

public class SplashActivity extends BaseActivity<LoginPresenter> {

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(new LoginRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return 0;
    }

    @Override
    public void initData() {
        if ((boolean) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                .getApplication(), CommonConstants.FIRST_LOGIN, true, CommonConstants.USER_INFO_PRE_NAME)) {
//            PushClientHelper.getInstance(this).registerPushService(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            String phoneNum = (String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                    .getApplication(), CommonConstants.USER_PHONE, "", CommonConstants.USER_INFO_PRE_NAME);
            String pwd = (String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                    .getApplication(), CommonConstants.USER_PWD, "", CommonConstants.USER_INFO_PRE_NAME);
            if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(pwd)) {
//                PushClientHelper.getInstance(this).registerPushService(null);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
//                getPresenter().login(Message.obtain(this, new String[]{phoneNum, pwd}), true, areaNum.replace("+", ""));
            }
        }
    }

    @Override
    public void initViews() {
    }

    @Override
    public void initEvents() {
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case MSG_LOGIN:
                if (((LoginBean) message.obj).getAccess_token() != null) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, (message.obj).toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
