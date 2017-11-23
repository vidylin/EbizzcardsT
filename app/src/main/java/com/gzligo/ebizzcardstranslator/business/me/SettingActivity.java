package com.gzligo.ebizzcardstranslator.business.me;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.utils.DataCleanUtils;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/1.
 */

public class SettingActivity extends BaseActivity<SettingPresenter>{
    private static final int LOGOUT_RESULT = 0x23;
    @BindView(R.id.setting_clear_cache_txt)TextView mCacheNumTxt;
    @BindView(R.id.setting_version_txt)TextView mVersionTxt;

    @Override
    public SettingPresenter createPresenter() {
        return new SettingPresenter(new SettingRepository());
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {
        mCacheNumTxt.setText(getCacheSize());
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.setting_notification_rl,R.id.setting_languages_rl,
            R.id.setting_clear_cache_rl,R.id.setting_version_rl,R.id.setting_logout_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.setting_notification_rl:
                startActivity(new Intent(this,SettingNotificationActivity.class));
                break;
            case R.id.setting_languages_rl:
                startActivity(new Intent(this,SettingLanguagesActivity.class));
                break;
            case R.id.setting_clear_cache_rl:
                clearCache();
                break;
            case R.id.setting_version_rl:
                break;
            case R.id.setting_logout_rl:
                getPresenter().logout(Message.obtain(this));
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case LOGOUT_RESULT:
                new SharedPreferencesUtils().putSharedPreference(this, CommonConstants.FIRST_LOGIN,true,CommonConstants.USER_INFO_PRE_NAME);
                new SharedPreferencesUtils().putSharedPreference(this, CommonConstants.USER_PWD,"",CommonConstants.USER_INFO_PRE_NAME);
                Intent outIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                outIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(outIntent);
                finish();
                break;
        }
    }

    private void clearCache() {
        Toast.makeText(this, getResources().getString(R.string.setting_clear_cache), Toast.LENGTH_SHORT).show();
        DataCleanUtils.clearAllCache(this);

        if (mCacheNumTxt != null) {
            mCacheNumTxt.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCacheNumTxt.setText(getCacheSize());


                }
            }, 50);
        }
    }

    private String getCacheSize() {
        try {
            return DataCleanUtils.getTotalCacheSize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
