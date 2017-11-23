package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;
import android.widget.CompoundButton;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.TxtSwitch;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/1.
 */

public class SettingNotificationActivity extends BaseActivity implements IView{

    @BindView(R.id.notification_detail_switch)
    TxtSwitch mNotificationDetail;
    @BindView(R.id.notification_sound_switch)
    TxtSwitch mSound;
    @BindView(R.id.notification_vibrate_switch)
    TxtSwitch mVibrate;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_notification;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {
        mSound.setChecked(PreferencesUtils.getPrefBoolean(this, TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_SOUND, true));
        mNotificationDetail.setChecked(PreferencesUtils.getPrefBoolean(this, TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_NOTIFY_DETAIL, true));
        mVibrate.setChecked(PreferencesUtils.getPrefBoolean(this, TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_VIBRATE, true));
    }

    @Override
    public void initEvents() {
        mNotificationDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.setPrefBoolean(SettingNotificationActivity.this,TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_NOTIFY_DETAIL,isChecked);
            }
        });
        mSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.setPrefBoolean(SettingNotificationActivity.this,TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_SOUND,isChecked);
            }
        });
        mVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.setPrefBoolean(SettingNotificationActivity.this,TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_VIBRATE,isChecked);
            }
        });
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
