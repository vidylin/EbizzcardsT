package com.gzligo.ebizzcardstranslator.business.wallet;

import android.content.Intent;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.business.account.view.login.ForgetPhoneActivity;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.persistence.District;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/12.
 */

public class MyWalletPasswordManagerActivity extends BaseActivity implements IView {

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_my_wallet_password;
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

    @OnClick({R.id.tv_close, R.id.my_wallet_change_pwd_rl, R.id.my_wallet_forget_pwd_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.my_wallet_change_pwd_rl:
                startActivity(new Intent(this, BankCardPasswordSettingActivity.class)
                        .putExtra("FROM", "MyWalletPasswordManagerActivity"));
                break;
            case R.id.my_wallet_forget_pwd_rl:
                String areaNum = (String) SharedPreferencesUtils.getSharedPreferences(this,
                        CommonConstants.USER_COUNTRY_ID, "", CommonConstants.USER_INFO_PRE_NAME);
                String areaName = (String) SharedPreferencesUtils.getSharedPreferences(this,
                        CommonConstants.USER_COUNTRY_NAME, "", CommonConstants.USER_INFO_PRE_NAME);
                District district = new District();
                district.setAreaNumber(areaNum);
                district.setLocalName(areaName);
                startActivity(new Intent(this, ForgetPhoneActivity.class)
                        .putExtra("DISTRICT", district)
                        .putExtra("FROM", "MyWalletPasswordManagerActivity"));
                break;
        }
    }
}
