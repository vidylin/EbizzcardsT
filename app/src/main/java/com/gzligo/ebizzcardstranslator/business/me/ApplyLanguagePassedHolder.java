package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguageSkillBean;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import java.util.TreeMap;

import butterknife.BindView;

/**
 * Created by ZuoJian on 2017/7/14.
 */

public class ApplyLanguagePassedHolder extends BaseHolder<LanguageSkillBean> {

    @BindView(R.id.apply_language_passed_item_tv) TextView mLanguageNameTv;
    @BindView(R.id.application_language_passed_tv) TextView mLanguageTitleTv;

    private TreeMap<Integer,LanguagesBean> beanTreeMap;

    public ApplyLanguagePassedHolder(View itemView,TreeMap<Integer,LanguagesBean> beanTreeMap) {
        super(itemView);
        this.beanTreeMap = beanTreeMap;
    }

    @Override
    public void setData(LanguageSkillBean data, int position) {
        if (position==0){
            mLanguageTitleTv.setVisibility(View.VISIBLE);
        }else {
            mLanguageTitleTv.setVisibility(View.GONE);
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
