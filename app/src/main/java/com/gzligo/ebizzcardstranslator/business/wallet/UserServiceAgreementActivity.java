package com.gzligo.ebizzcardstranslator.business.wallet;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebSettings;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.common.ProgressWebView;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class UserServiceAgreementActivity extends BaseActivity {
    @BindView(R.id.agreement_content) ProgressWebView agreementContent;
    @BindView(R.id.agreement_actionbar) ToolActionBar mActionbar;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_user_service_agreement;
    }

    @Override
    public void initData() {
        String url;
        if (getIntent().getStringExtra(TranslatorConstants.Common.DATA)==null|| TextUtils.isEmpty(getIntent().getStringExtra(TranslatorConstants.Common.DATA))) {
            url = "http://ebizzcards.com/protocol/?page=proto_transpay";
        }else {
            url = getIntent().getStringExtra(TranslatorConstants.Common.DATA);
        }
        String language = LanguageUtils.getLanguage(this);
        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            url = url + "&lang=ch";
        }else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
            url = url + "&lang=en";
        }
        if (getIntent().getStringExtra("actionbar_name")==null|| TextUtils.isEmpty(getIntent().getStringExtra("actionbar_name"))){
            mActionbar.setTitleTxt(getIntent().getStringExtra("actionbar_name"));
        }
        agreementContent.loadUrl(url);
    }

    @Override
    public void initViews() {
        WebSettings webSettings = agreementContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setTextZoom(200);
    }

    @Override
    public void initEvents() {
    }

    @OnClick({R.id.tv_close})
    void onClick() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && agreementContent.canGoBack()) {
            agreementContent.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
