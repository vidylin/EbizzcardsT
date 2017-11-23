package com.gzligo.ebizzcardstranslator.business.me;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSelectBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by ZuoJian on 2017/7/14.
 */

public class ApplyLanguageUnconfirmedHolder extends BaseHolder<LanguageSkillBean> {

    @BindView(R.id.apply_language_unconfirmed_tv) TextView mLanguageNameTv;
    @BindView(R.id.application_language_unconfirmed_tv) TextView mLanguageTitleTv;
    @BindView(R.id.apply_language_unconfirmed_iv) ImageView checkedIv;

    private TreeMap<Integer,LanguagesBean> beanTreeMap;
    private int titlePosition;

    public ApplyLanguageUnconfirmedHolder(View itemView,TreeMap<Integer,LanguagesBean> beanTreeMap,int titlePosition) {
        super(itemView);
        this.beanTreeMap = beanTreeMap;
        this.titlePosition = titlePosition;
    }

    @Override
    public void setData(LanguageSkillBean data, int position) {
        if (titlePosition == position){
            mLanguageTitleTv.setVisibility(View.VISIBLE);
        }else {
            mLanguageTitleTv.setVisibility(View.GONE);
        }
        if (data.getStatus()==1){
            checkedIv.setBackgroundResource(R.mipmap.apply_language_unconfirmed_selected);
        }else {
            checkedIv.setBackgroundResource(R.mipmap.apply_language_unconfirmed_nomarl);
        }
        if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            mLanguageNameTv.setText(beanTreeMap.get(data.getLanguage_id()).getZh_name());
        }else if (LanguageUtils.getLanguage(AppManager.get().getTopActivity()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
            mLanguageNameTv.setText(beanTreeMap.get(data.getLanguage_id()).getEn_name());
        }
    }

    @Override
    public void onRelease() {

    }
}
