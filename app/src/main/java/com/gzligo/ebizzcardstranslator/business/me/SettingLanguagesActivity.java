package com.gzligo.ebizzcardstranslator.business.me;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.BaseApplication;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ZuoJian on 2017/6/1.
 */

public class SettingLanguagesActivity extends BaseActivity implements IView{

    @BindView(R.id.setting_language_zh_iv)
    ImageView mCheckIvZh;
    @BindView(R.id.setting_language_en_iv)
    ImageView mCheckIvEn;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_setting_languages;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViews() {
        String language = LanguageUtils.getLanguage(this);
        if(language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)){
            mCheckIvEn.setVisibility(View.GONE);
            mCheckIvZh.setVisibility(View.VISIBLE);
        }else {
            mCheckIvEn.setVisibility(View.VISIBLE);
            mCheckIvZh.setVisibility(View.GONE);
        }
    }

    @Override
    public void initEvents() {

    }

    @OnClick({R.id.tv_close,R.id.setting_language_zh_rl,R.id.setting_language_en_rl})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
            case R.id.setting_language_zh_rl:
                handleSwitchZhLanguage();
                break;
            case R.id.setting_language_en_rl:
                handleSwitchEnLanguage();
                break;
        }
    }

    private void handleSwitchEnLanguage() {
        mCheckIvEn.setVisibility(View.VISIBLE);
        mCheckIvZh.setVisibility(View.GONE);
        switchLanguage(TranslatorConstants.SharedPreferences.LANGUAGE_EN);
    }

    private void handleSwitchZhLanguage() {
        mCheckIvEn.setVisibility(View.GONE);
        mCheckIvZh.setVisibility(View.VISIBLE);
        switchLanguage(TranslatorConstants.SharedPreferences.LANGUAGE_CH);
    }

    public void switchLanguage(String language) {
        LanguageUtils.switchLanguage(getApplicationContext(), language);
        restartApplication(this);
        finish();
    }

    public void restartApplication(Activity exActivity) {
        LinkedList<Activity> linkedList = new LinkedList<>();
        linkedList.addAll(AppManager.get().getActivities());
        for (int i = 0; i < linkedList.size(); i++) {
            Activity activity = linkedList.get(i);
            if (exActivity != null && exActivity == activity) {
                continue;
            }
            activity.finish();
            Intent intent = activity.getIntent();
            intent.setComponent(new ComponentName(activity, activity.getClass()));
            activity.startActivity(intent);
        }
    }
}
